package jp.ac.titech.cs.se.nakamura.cfgen.cabocha.dto

import jp.ac.titech.cs.se.nakamura.cfgen.cabocha.{Cabocha}
import org.specs2.Specification
import scala.collection.JavaConverters._
class SentenceSpec extends Specification {
  def is=s2"""$sequential
            動詞を正しく抽出できるか $test
  """

  def test = {

   val results =  List("犬が歩く", "利用者がビデオを借りる", "あの日見た花の名前を僕たちはまだ知らない")
      .map(l => Cabocha analyze l)

    val verbs = results flatMap (r => r.getIndepVerbs) map (v => v.base)

    verbs(0) must_== "歩く"
    verbs(1) must_== "借りる"
    verbs(2) must_== "知る"
  }

}
