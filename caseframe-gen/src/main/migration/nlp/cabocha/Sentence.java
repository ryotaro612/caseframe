package jp.ac.titech.cs.se.nlp.cabocha;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.ac.titech.cs.se.nlp.util.CaseUtil;
import edu.stanford.nlp.util.StringUtils;

/**
 * cabocha における一文．
 * 
 * @author hayashi
 */
public class Sentence {
    /**
     * 文に所属している文節群．
     */
    private final List<Chunk> chunks = new ArrayList<Chunk>();

    public Sentence() {
    }

    public static Sentence parse(List<String> lines) {
        final Sentence sentence = new Sentence();

        Chunk chunk = null;
        for (final String line : lines) {
            //line = line.trim();

            if (line.equals("EOS")) {
                // finished
                break;
            }
            if (Chunk.isChunkLine(line)) {
                // for chunk
                chunk = Chunk.parse(line);
                sentence.addChunk(chunk);
            } else {
                // for token
                final Token token = Token.parse(line);
                if (chunk == null) {
                    throw new IllegalArgumentException("Illegal format");
                } else {
                    chunk.addToken(token);
                }
            }
        }

        sentence.buildCache();
        return sentence;
    }

    @Override
    public String toString() {
        return toSurfaceString();
    }

    public String toSurfaceString() {
        final StringBuilder sb = new StringBuilder();
        for (final Chunk c : chunks) {
            sb.append(c.toSurfaceString());
        }
        return sb.toString();
    }

    public void addChunk(Chunk chunk) {
        chunks.add(chunk);
        chunk.setParent(this);
    }

    public List<Chunk> getChunks() {
        return Collections.unmodifiableList(chunks);
    }

    public void buildCache() {
        for (final Chunk src : chunks) {
            final Chunk dst = src.getLink();
            if (dst != null) {
                dst.addInverseLink(src);
            }
        }
    }

    public String toTreeString() {
        final List<String> result = new ArrayList<String>();
        for (final Chunk c : chunks) {
            if (c.getLink() == null) {
                result.add(c.toTreeString());
            }
        }
        return StringUtils.join(result, " ");
    }

    /**
     * 表層格となるチャンクのリストを返す
     * @return
     */
    public List<Chunk> getSurfaceCaseChunks() {
        List<Chunk> res = new ArrayList<Chunk>();
        for (Chunk c : getChunks()) {
            if (CaseUtil.isSurfaceCase(c.getSurfaceCase())) {
                res.add(c);
            }
        }
        return res;
    }
}
