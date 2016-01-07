package jp.ac.titech.cs.se.nlp.entity;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.titech.cs.se.nlp.cabocha.Sentence;
import jp.ac.titech.cs.se.nlp.strategy.NlpStrategy;
import net.arnx.jsonic.JSON;

/**
 * 格フレーム
 * 
 * @author rtakizawa
 */
public class CaseFrame implements Comparable<CaseFrame> {

    /**
     *  深層格と格スロットのマップ
     */
    private Map<String, CaseSlot> slotMap;

    /**
     *  動詞
     */
    private String verb;

    /**
     * 点数
     */
    private double score;

    /**
     * 点数付け戦略
     */
    private NlpStrategy strategy;

    public CaseFrame(String verb, String slotinfo) {
        this.verb = verb;
        slotMap = parse(slotinfo);
    }

    /**
     * スロット情報をパースしてマップを作成する
     * 
     * @param slotinfo
     * @return
     */
    public static Map<String, CaseSlot> parse(String slotinfo) {
        Map<String, CaseSlot> map = new HashMap<String, CaseSlot>();
        Map<String, List<Object>> slotMap = JSON.decode(slotinfo);
        for (Map.Entry<String, List<Object>> e : slotMap.entrySet()) {
            map.put(e.getKey(), new CaseSlot(e.getValue()));
        }
        return map;
    }

    /**
     * 格の順序を適切に並べたエントリーのリストを返す
     * 
     * @return
     */
    public List<Map.Entry<String, CaseSlot>> sort() {
        //TODO: 作る
        return null;
    }

    private static final DecimalFormat scoreForm = new DecimalFormat("0.00");

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("(");
        for (Map.Entry<String, CaseSlot> e : slotMap.entrySet()) {
            sb.append(e.getKey() + ": ");
            sb.append(e.getValue().toString());
            sb.append(", ");
        }
        sb.deleteCharAt(sb.length() - 2);
        sb.append(")");
        return scoreForm.format(score) + " : " + verb + sb.toString();
    }

    public Map<String, CaseSlot> getSlotMap() {
        return Collections.unmodifiableMap(slotMap);
    }

    public double getScore() {
        return score;
    }

    /**
     * 点数付け戦略のセット
     * @param s
     */
    public void setStrategy(NlpStrategy s) {
        strategy = s;
    }

    /**
     * chunksに対するこの格フレームの点数
     * @param chunks
     */
    public void eval(Sentence s) {
        score = strategy.eval(this, s);
    }

    @Override
    public int compareTo(CaseFrame other) {
        return (int) ((other.getScore() - score) * 100);
    }
}
