package jp.ac.titech.cs.se.nakamura.cfgen.dto

import jp.ac.titech.cs.se.nakamura.cfgen.dao.{CpcDao, JwdDao}
import jp.ac.titech.cs.se.nakamura.cfgen.dto.edr._
import jp.ac.titech.cs.se.nakamura.cfgen.dto.edr.AtomicFormula
import jp.ac.titech.cs.se.nakamura.cfgen.dto.edr.OrFormula
import jp.ac.titech.cs.se.nakamura.cfgen.cabocha.dto.Sentence
import jp.ac.titech.cs.se.nakamura.cfgen.dao.ConceptId
import scala.Some
import jp.ac.titech.cs.se.nakamura.cfgen.dto.edr.AndFormula
import org.slf4j.LoggerFactory


/**
 * TODO このケースクラスは
 */
case class CaseFrame(private val sentence: Sentence
                     ,private[dto] val sentVerbId: ConceptId
                     ,private[dto] val  caseFrame: edr.CaseFrame) {

  val slots = caseFrame.slotInfo map (caseSlot => {
    sentence.chunks collect {
      case chunk if chunk.containsUniCase(caseSlot.surfaceCase) && chunk.containsUniNoun
        => chunk.tokens.filter(t => t.isNoun)(0)
    } match {
      case x::Nil => (caseSlot, Some(x))
      case _ => (caseSlot, None)
    }
  })

  val score: Double = EasyStrategy.eval(this)

}

object EasyStrategy {
  private val logger = LoggerFactory.getLogger(EasyStrategy.getClass)
  def eval(cf: CaseFrame): Double = {
    val total: Double = cf.slots.foldLeft(0.toDouble)((s, pair) => pair._2 match {
      case None => s + 0.toDouble
      case Some(token) => JwdDao.getConceptIds(token.base).map(cId => calc(pair._1.formula, cId)).max
    })
    (total / cf.slots.size) / (CpcDao.getDistance(cf.sentVerbId, cf.caseFrame.id).dst + 1)
  }

  private def calc(formula: Formula, cId: ConceptId): Double  = {
    formula match {
      case atom: AtomicFormula =>
        // logger.debug("distance: {}, {}, {}, {}"
        // , CpcDao.getDistance(atom.id, cId), atom.id, cId,
        // (1.toDouble / (CpcDao.getDistance(atom.id, cId).dst + 1)).toString)
        1.toDouble / (CpcDao.getDistance(atom.id, cId).dst + 1)
      case or: OrFormula => or.formulas.map(f => calc(f, cId)).max
      case and: AndFormula => and.formulas.map(f => calc(f, cId)).min
      case not: NotFormula => 1 - calc(not.formula, cId)
    }
  }
}
