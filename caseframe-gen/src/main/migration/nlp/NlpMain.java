package jp.ac.titech.cs.se.nlp;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import jp.ac.titech.cs.se.nlp.cabocha.Cabocha;
import jp.ac.titech.cs.se.nlp.cabocha.Chunk;
import jp.ac.titech.cs.se.nlp.cabocha.Sentence;
import jp.ac.titech.cs.se.nlp.db.JCPDao;
import jp.ac.titech.cs.se.nlp.db.JWDDao;
import jp.ac.titech.cs.se.nlp.entity.CaseFrame;
import jp.ac.titech.cs.se.nlp.strategy.EasyStrategy;
import jp.ac.titech.cs.se.nlp.strategy.NlpStrategy;

public class NlpMain {

    public static void main(String[] args) {
        new NlpMain().exec(args);
    }

    public void exec(String[] args) {
        try {
            Cabocha cabo = new Cabocha();

            // 辞書とのマッチがうまくいくケース。
            Sentence s = cabo.analyze("店員が商品の在庫情報を端末で確認する");
            eval(s);

            // 要求文があいまいで辞書との相性が悪い。
            // といってもこういう要求文は実際的にはありうるのでどうにか対応する必要はありそう。
            s = cabo.analyze("商品の在庫を確認する");
            eval(s);

            // 複数の概念をもつ単語があるためprecisionが下がる。
            //TODO:
            // これはユーザーに単語の概念として使用するものを入力してもらえば解決できる。
            // ただしその仕様がいいかどうかについては検討の余地あり。
            // どっちかというと、一定以上のスコアをもつものをそのまま採用するか、ユーザーに選ばせるとかのほうがいい気がする。
            s = cabo.analyze("個人情報取得事業者が会員から個人情報を取得する");
            eval(s);

            // 動詞トークンが二つ以上ある場合でも、係り受けがあるかどうかを判断することで適切な動詞句を抜き出せる。
            s = cabo.analyze("入荷した商品を登録する");
            eval(s);

            // 以下は辞書が対応していない例。
            s = cabo.analyze("システムがリクエストに素早く応答する");
            eval(s);
            s = cabo.analyze("システムが不正なリクエストを拒否する");
            eval(s);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 文を評価し結果を出力する
     * @param s
     */
    public void eval(Sentence s) {
        String verb = getVerbChunk(s).toSurfaceString();
        List<Integer> ids = new JWDDao().getConceptIdList(verb);
        List<CaseFrame> frames = new JCPDao().getCaseFrameList(ids);
        NlpStrategy strategy = new EasyStrategy();
        for (CaseFrame frame : frames) {
            frame.setStrategy(strategy);
            frame.eval(s);
        }
        Collections.sort(frames);
        System.out.println("「" + s.toString() + "」");
        for (CaseFrame frame : frames) {
            System.out.println(frame.toString());
        }
        System.out.println("");
    }

    /**
     * sentence中の動詞句を返す
     * @param sentence
     * @return
     */
    public Chunk getVerbChunk(Sentence sentence) {
        for (Chunk c : sentence.getChunks()) {
            if (c.isVerb()) {
                return c;
            }
        }
        return null;
    }

}
