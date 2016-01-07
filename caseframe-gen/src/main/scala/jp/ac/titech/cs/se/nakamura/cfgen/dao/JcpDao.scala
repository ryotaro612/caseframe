package jp.ac.titech.cs.se.nakamura.cfgen.dao

import scala.collection.mutable
import org.jooq.Record
import scala.Some
import jp.ac.titech.cs.se.nakamura.cfgen.dto.edr.CaseFrame


object JcpDao {
  import DicDBConnector.create
  import jp.ac.titech.cs.se.nakamura.cfgen.dao.tables.Jcp.JCP

  private val cache = mutable.HashMap[ConceptId, List[CaseFrame]]()

  /**
   * TODO 他のDBへのSQL文発行処理と共通化すること
   */
  def getCaseFrameList(concept: ConceptId): List[CaseFrame] = {
    cache get concept match {
      case Some(caseFrames) => caseFrames
      case None =>
        val results: List[CaseFrame] = create.select(JCP.ACT_NAME, JCP.SLOTINFO).from(JCP)
          .where(JCP.ACT_CONCEPT eq concept.id).fetch().toArray.toList.map {
          case r: Record =>
            CaseFrame(concept, r.getValue(JCP.ACT_NAME), r.getValue(JCP.SLOTINFO))
        }
        cache += (concept -> results)
        results
    }
  }

}



