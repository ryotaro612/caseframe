package jp.ac.titech.cs.se.nlp.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.titech.cs.se.nlp.cabocha.Chunk;
import jp.ac.titech.cs.se.nlp.cabocha.Sentence;
import jp.ac.titech.cs.se.nlp.cabocha.Token;
import jp.ac.titech.cs.se.nlp.db.JWDDao;
import jp.ac.titech.cs.se.nlp.entity.CaseFrame;
import jp.ac.titech.cs.se.nlp.entity.CaseSlot;

/**
 * Easy
 *  - スコア計算アルゴリズム
 *   + 格スロットにマッチするチャンクの数を計算(=matches)
 *   + scoreを min(格スロットの数, 原文の表層格の数)で割る(格スロットの埋まった率)
 *   + そこから、(原文の表層格の数 - スロット数) * 0.1を引く(省略などを考慮)
 *   + 0以下になった場合は0
 *  
 * @author rtakizawa
 */
public class EasyStrategy implements NlpStrategy {

    @Override
    public double eval(CaseFrame frame, Sentence s) {
        int matches = 0;
        for (CaseSlot slot : frame.getSlotMap().values()) {
            for (Chunk c : getSlotChunks(s)) {
                if (slot.getSurfaceCase().equals(c.getSurfaceCase())) {
                    matches += evalSlot(slot, c);
                }
            }
        }
        int surfaces = s.getSurfaceCaseChunks().size();
        int slots = frame.getSlotMap().size();
        int divisor = (surfaces > slots) ? slots : surfaces;
        double score = ((double) matches / divisor) - 0.1 * abs(surfaces - slots);
        return (score > 0) ? score : 0;
    }

    private int abs(int i) {
        return (i > 0) ? i : -i;
    }

    private List<Chunk> getSlotChunks(Sentence s) {
        List<Chunk> chunks = new ArrayList<Chunk>();
        for (Chunk c : s.getChunks()) {
            if (!c.isVerb()) {
                chunks.add(c);
            }
        }
        return chunks;
    }

    private int evalSlot(CaseSlot slot, Chunk c) {
        for (int id : getChunkConcepts(c)) {
            if (slot.accept(id)) {
                return 1;
            }
        }
        return 0;
    }

    private Map<Chunk, List<Integer>> cache = new HashMap<Chunk, List<Integer>>();

    private List<Integer> getChunkConcepts(Chunk c) {
        if (cache.containsKey(c)) {
            return cache.get(c);
        }

        JWDDao dao = new JWDDao();
        List<String> words = new ArrayList<String>();
        List<Integer> res = new ArrayList<Integer>();
        for (Token t : c.getTokens()) {
            if (!t.isParticle() && !t.isVerb()) {
                words.add(t.getSurface());
            }
        }
        for (int i = 0; i < words.size(); i++) {
            String word = "";
            for (int j = i; j < words.size(); j++) {
                word += words.get(j);
            }
            res = dao.getConceptIdList(word);
            if (!res.isEmpty()) {
                cache.put(c, res);
                return res;
            }
        }
        cache.put(c, res);
        return res;
    }

}
