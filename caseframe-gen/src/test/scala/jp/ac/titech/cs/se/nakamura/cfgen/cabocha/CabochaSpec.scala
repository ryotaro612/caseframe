package jp.ac.titech.cs.se.nakamura.cfgen.cabocha

import org.specs2.Specification


class CabochaSpec extends Specification {

  def is =s2"""$sequential
         cabochaの出力テスト $test
  """

  def test = {
    val a = Cabocha.analyze("犬が歩く")
    println(a)
    a must_!= null
  }
}
