package jp.ac.titech.cs.se.nlp.util;

import java.util.Arrays;
import java.util.List;

//TODO:
// これいいのか微妙。
// いろんなとこで使いそうだから一か所にまとめておく必要があるけど
// ユーティリティとしておくのがいいのか微妙。

/**
 * 格に関するユーティリティクラス
 * 
 * @author rtakizawa
 */
public class CaseUtil {

    //TODO: これで全部かどうかわからん
    private static List<String> surfaceCases = Arrays.asList(
            "が", "は", "を", "に", "から", "へ", "と", "より", "まで", "で");
    
    public static boolean isSurfaceCase(String s) {
        return surfaceCases.contains(s);
    }

}
