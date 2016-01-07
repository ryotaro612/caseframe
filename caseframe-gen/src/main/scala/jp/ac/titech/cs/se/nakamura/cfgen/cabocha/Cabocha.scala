package jp.ac.titech.cs.se.nakamura.cfgen.cabocha

import jp.ac.titech.cs.se.nakamura.cfgen.Properties
import java.io.{InputStreamReader, BufferedReader, OutputStreamWriter}
import java.nio.charset.Charset
import scala.collection.mutable.ArrayBuffer
import jp.ac.titech.cs.se.nakamura.cfgen.cabocha.dto.Sentence


object Cabocha {
  val builder = new ProcessBuilder(Properties("cabocha.path").get, "-f1", "-n1")
  val charSet = Properties("cabocha.encoding").get
  builder.redirectErrorStream(true)

  val process = builder.start()
  val writer =  new OutputStreamWriter(process.getOutputStream, Charset.forName(charSet))
  val reader = new BufferedReader(new InputStreamReader(process.getInputStream, charSet))


  def analyze(input: String): Sentence = {
     Sentence(execute(input))
  }
  def execute(input: String): List[String] = {
    writer.write(input + System.lineSeparator())
    writer.flush()

    val results = new ArrayBuffer[String]()

    var buffer: String = null
    while({buffer = reader.readLine(); buffer} != "EOS") {
      results += buffer
    }
    results.toList
  }
  def close() {
    writer.close()
    process.waitFor()
  }
}

