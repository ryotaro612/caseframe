package jp.ac.titech.cs.se.nlp.cabocha;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.stanford.nlp.util.StringUtils;

/**
 * cabocha における文節．
 */
public class Chunk {
    /**
     * 所属している文．
     */
    private Sentence parent;

    /**
     * 文節番号．
     */
    private final int id;

    /**
     * 係り受け先．
     */
    private final int link;

    /**
     * 係り受けの逆方向リンクキャッシュ．後付けで構築される．
     */
    private final List<Chunk> inverseLinks = new ArrayList<Chunk>();

    /**
     * 
     */
    private final String rel;

    /**
     * 主辞を表す形態素の番号．
     */
    private final int head;

    /**
     * 機能語を表す形態素の番号．
     */
    private final int func;

    /**
     * 係り受けのスコア．
     */
    private final double score;

    /**
     * 所属する形態素群．
     */
    private final List<Token> tokens = new ArrayList<Token>();

    public Chunk(int id, int link, String rel, int head, int func, double score) {
        this.id = id;
        this.link = link;
        this.rel = rel;
        this.head = head;
        this.func = func;
        this.score = score;
    }

    public static Chunk parse(String line) {
        final String[] a = line.split(" ");

        // Example: * 0 1D 0/1 0.000000
        // * [id] [link][rel] [head]/[func] [score]

        final int id = Integer.parseInt(a[1]);
        final int link = Integer.parseInt(a[2].substring(0, a[2].length() - 1));
        final String rel = a[2].substring(a[2].length() - 1);
        final String[] hf = a[3].split("/");
        final int head = Integer.parseInt(hf[0]);
        final int func = Integer.parseInt(hf[1]);
        final double score = Double.parseDouble(a[4]);

        return new Chunk(id, link, rel, head, func, score);
    }

    @Override
    public String toString() {
        return String.format("* %d %d%s %d/%d %f", id, link, rel, head, func, score);
    }

    /**
     * このチャンクが表す句の文字列を返す
     * @return
     */
    public String toSurfaceString() {
        final StringBuilder sb = new StringBuilder();
        for (final Token t : tokens) {
            sb.append(t.getSurface());
        }
        return sb.toString();
    }

    /**
     * このチャンクの表層格を表す文字列を返す
     * @return
     */
    public String getSurfaceCase() {
        for (Token t : getTokens()) {
            if (t.isParticle()) {
                return t.getSurface();
            }
        }
        return null;
    }

    public String toTreeString() {
        if (inverseLinks.size() > 0) {
            final List<String> result = new ArrayList<String>();
            for (final Chunk c : inverseLinks) {
                result.add(c.toTreeString());
            }
            return String.format("(%s %s)", toSurfaceString(), StringUtils.join(result, " "));
        } else {
            return String.format("(%s)", toSurfaceString());
        }
    }

    public static boolean isChunkLine(String line) {
        return line.startsWith("* ");
    }

    public void addToken(Token token) {
        tokens.add(token);
        token.setParent(this);
    }

    /**
     * この句が動詞句であるかどうか判定する．
     * 品詞が「動詞」で始まる単語を含み，かつどこにも係っていない場合に動詞句であると判断する．
     * TODO もっと洗練させる必要がある
     * @return
     */
    public boolean isVerb() {
        for (final Token t : tokens) {
            if (t.isVerb() && !isLinked()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isLinked() {
        return getLink() != null;
    }

    public List<Token> getTokens() {
        return Collections.unmodifiableList(tokens);
    }

    public int getId() {
        return id;
    }

    public int getLinkId() {
        return link;
    }

    public Chunk getLink() {
        return link == -1 ? null : parent.getChunks().get(link);
    }

    public List<Chunk> getInverseLink() {
        return Collections.unmodifiableList(inverseLinks);
    }

    public void addInverseLink(Chunk chunk) {
        inverseLinks.add(chunk);
    }

    public String getRel() {
        return rel;
    }

    public double getScore() {
        return score;
    }

    public int getHead() {
        return head;
    }

    public int getFunc() {
        return func;
    }

    public Sentence getParent() {
        return parent;
    }

    public void setParent(Sentence parent) {
        this.parent = parent;
    }
}
