package jp.ac.titech.cs.se.nlp.cabocha;

import org.apache.commons.lang3.StringUtils;

/**
 * chasen における形態素．
 */
public class Token {

    public enum Feature {
        POS,
        POS_1,
        POS_2,
        POS_3,
        CTYPE,
        CFORM,
        BASE,
        READ,
        PRON;
    };

    /**
     * 所属している文節．
     */
    private Chunk parent;

    /**
     * 表層形．
     */
    private final String surface;

    /**
     * 品詞．
     * "品詞,品詞細分類1,品詞細分類2,品詞細分類3,活用形,活用型,原形,読み,発音" と並んでいる．
     */
    private final String[] features;

    /**
     * 固有表現タグ．
     */
    private final String ne;


    public Token(String surface, String[] features, String ne) {
        this.surface = surface;
        this.features = features;
        this.ne = ne;
    }

    public static Token parse(String line) {
        final String[] a = line.split("\t");

        // 表層形     品詞,品詞細分類1,品詞細分類2,品詞細分類3,活用形,活用型,原形,読み,発音  固有表現タグ
        // [surface]  [pos][ctype1][cform2][3][katsu][katusou],[base],[read],[pro]    [ne]
        final String surface = a[0];
        final String[] features = a[1].split(",");
        final String ne = a[2];

        return new Token(surface, features, ne);
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\t%s", surface, StringUtils.join(features, ","), ne);
    }

    public String toSurfaceString() {
        return getSurface();
    }

    public void setParent(Chunk chunk) {
        this.parent = chunk;
    }

    public Chunk getParent() {
        return parent;
    }

    /**
     * 表層形を返す．
     * @return
     */
    public String getSurface() {
        return surface;
    }

    public String[] getFeatures() {
        return features;
    }

    /**
     * 固有表現タグを返す．
     */
    public String getNE() {
        return ne;
    }

    public String getFeature(Feature key) {
        return features[key.ordinal()];
    }

    /**
     * 品詞の種類を返す
     * @return
     */
    public String getPartOfSpeech() {
        return getFeature(Feature.POS);
    }
    
    /**
     * 動詞か
     * @return
     */
    public boolean isVerb() {
        return getPartOfSpeech().equals("動詞");
    }
    
    /**
     * 助詞か
     * @return
     */
    public boolean isParticle() {
        return getPartOfSpeech().equals("助詞");
    }
    
    public String getPos1() {
        return getFeature(Feature.POS_1);
    }

    public String getPos2() {
        return getFeature(Feature.POS_2);
    }

    public String getPos3() {
        return getFeature(Feature.POS_3);
    }

    /**
     * 品詞を返す．
     */
    public String getPos() {
        String result = getPartOfSpeech();

        final String pos1 = getPos1();
        result += pos1.equals("*") ? "" : "-" + pos1;

        final String pos2 = getPos2();
        result += pos2.equals("*") ? "" : "-" + pos2;

        final String pos3 = getPos3();
        result += pos3.equals("*") ? "" : "-" + pos3;

        return result;
    }

    /**
     * 活用を返す．
     */
    public String getConjugation() {
        return getFeature(Feature.CTYPE);
    }

    /**
     * 活用形を返す．
     */
    public String getConjugate() {
        return getFeature(Feature.CFORM);
    }

    /**
     * 原形を返す．
     */
    public String getBase() {
        return getFeature(Feature.BASE);
    }

    /**
     * 読みを返す．
     */
    public String getRead() {
        return getFeature(Feature.READ);
    }

    /**
     * 発音を返す．
     */
    public String getPronunce() {
        return getFeature(Feature.PRON);
    }
}
