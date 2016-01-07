package jp.ac.titech.cs.se.nlp.cabocha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 係り受け解析器 cabocha のフロントエンド．
 * @author hayashi
 * @author inouew
 */
public class Cabocha {
    /**
     * cabocha 実行ファイルのデフォルトパス．省略時にはこれを用いる．
     */
    public static final String DEFAULT_CABOCHA_PATH = "\"cabocha\"";

    /**
     * cabocha プロセス．
     */
    private final Process process;

    /**
     * cabocha プロセスの入力に結合された Writer．
     */
    private final OutputStreamWriter writer;

    /**
     * cabocha プロセスの出力に結合された Reader.
     */
    private final BufferedReader reader;

    public Cabocha() throws IOException {
        this(DEFAULT_CABOCHA_PATH, "Shift_JIS");
    }

    /**
     * コンストラクタ．
     * @param executable cabocha 実行ファイルのパス．
     * @param charset cabocha が受付け，出力する文字列のエンコード名．Windows であれば "Shift_JIS" を指定する．
     * @throws java.io.IOException
     */
    public Cabocha(String executable, String charset) throws IOException {
        final ProcessBuilder builder = new ProcessBuilder(executable + " -f1");
        // 標準エラー出力をマージして出力する
        builder.redirectErrorStream(true);

        // cabocha を実行し，入力待ち状態にする．
        process = builder.start();
        writer = new OutputStreamWriter(process.getOutputStream(), Charset.forName(charset));
        reader = new BufferedReader(new InputStreamReader(process.getInputStream(), charset));
    }

    /**
     * cabocha を実行し，文字列を解析する．
     * @param input 解析対象の日本語一文
     * @return 解析結果のSentenceオブジェクト
     */
    public Sentence analyze(String input) throws IOException {
        return Sentence.parse(execute(input));
    }

    /**
     * cabocha に文を渡し，解析結果の文字列を行ごとのリスト形式で得る．
     */
    protected List<String> execute(String input) throws IOException {
        writer.write(input + "\r\n");
        writer.flush();

        final List<String> result = new ArrayList<String>();
        String buffer;
        while (!(buffer = reader.readLine()).equals("EOS")) {
            result.add(buffer);
        }
        return result;
    }

    /**
     * cabocha を終了する．
     * @throws InterruptedException
     */
    public void close() throws IOException, InterruptedException {
        writer.close();
        process.waitFor();
    }
}
