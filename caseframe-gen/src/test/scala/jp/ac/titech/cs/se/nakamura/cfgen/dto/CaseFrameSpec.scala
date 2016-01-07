package jp.ac.titech.cs.se.nakamura.cfgen.dto

import org.specs2.Specification
import jp.ac.titech.cs.se.nakamura.cfgen.cabocha.Cabocha
import jp.ac.titech.cs.se.nakamura.cfgen.dao.{JcpDao, JwdDao}

class CaseFrameSpec extends Specification {

  def is=s2"""$sequential
            格フレームを作るためのテスト$test
  """
  def test = {

   val a = List("犬が歩く") map (l => analyze(l))

    a must_!= null
  }


  def analyze(input: String) = {
    val sentence = Cabocha analyze input

    sentence.getIndepVerbs match {
      case head :: Nil =>
        (JwdDao getConceptIds head.base)
          .flatMap(concept => JcpDao.getCaseFrameList(concept).map(cf => CaseFrame(sentence, concept, cf)))
      case Nil => List()
      case head :: tail => List()
    }
  }
}
