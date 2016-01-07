package jp.ac.titech.cs.se.nakamura.cfgen

import jp.ac.titech.cs.se.nakamura.cfgen.cabocha.Cabocha
import org.slf4j.LoggerFactory
import jp.ac.titech.cs.se.nakamura.cfgen.dao.{ConceptId, CpcDao, JcpDao, JwdDao}
import jp.ac.titech.cs.se.nakamura.cfgen.dto.CaseFrame
import jp.ac.titech.cs.se.nakamura.cfgen.cabocha.dto.Token


object CaseFrameGen {
  private val logger  = LoggerFactory.getLogger(CaseFrameGen.getClass)

  def analyze(input: String) = {
    val sentence = Cabocha analyze input

    sentence.getIndepVerbs match {
      case head :: Nil =>
        (JwdDao getConceptIds head.base)
          .flatMap(concept => JcpDao.getCaseFrameList(concept).map(cf => CaseFrame(sentence,concept,  cf)))
      case Nil => List()
      case head :: tail => List()
    }
  }
  import jp.ac.titech.cs.se.nakamura.cfgen.dto.edr

  /**
   * TODO ちゃんとテストすること
   */
  private def getAltLowCaseFrames(id: ConceptId): List[edr.CaseFrame] = {
    getAltCaseFrames(id, c => CpcDao.getLowerConcepts(c))
  }
  /**
   * TODO ちゃんとテストすること
   */
  private def getAltUppwCaseFrames(id: ConceptId): List[edr.CaseFrame] = {
    getAltCaseFrames(id, c => CpcDao.getUpperConcepts(c))
  }
  private def getAltCaseFrames(id: ConceptId, f:  ConceptId => List[ConceptId]): List[edr.CaseFrame] = {
    f(id)  match {
      case Nil => Nil
      case alts => alts.foldLeft(List(): List[edr.CaseFrame])((frames, id)
      => frames ::: JcpDao.getCaseFrameList(id)) match {
        case Nil => alts.flatMap(c => getAltCaseFrames(c, f))
        case frames => frames
      }
    }
  }
  /*
  private def getAltCaseFrames(id: ConceptId) = {
    val lowerConcepts = CpcDao.getLowerConcepts(id)
    val upperConcepts = CpcDao.getUpperConcepts(id)

    val lower = CpcDao.getLowerConcepts(id).foldLeft(List(): List[CaseFrame])((frames, id)
      => frames ::: JcpDao.getCaseFrameList(id))

    val upper = CpcDao.getUpperConcepts(id).foldLeft(List(): List[CaseFrame])((frames, id)
      => frames ::: JcpDao.getCaseFrameList(id))

    lower ::: upper match {
      case Nil => (lowerConcepts.size != 0 || upperConcepts.size != 0) match {
        case true => getA
      }
    }
  }
  */
}
