package jp.ac.titech.cs.se.nakamura.cfgen.dao

import scala.collection.mutable
import jp.ac.titech.cs.se.nakamura.cfgen.dao.tables.Jwd.JWD
import org.jooq.Record

object JwdDao {
  import DicDBConnector.create
  // List select concept form jwd where name like ?

  private val cache = mutable.HashMap[String, List[ConceptId]]()

  /**
   * TODO コード共通化。インデントをきれいに
   */
  def getConceptIds(word: String): List[ConceptId] = {
    cache get word match {
      case Some(ids) => ids
      case  None =>
        val results: List[ConceptId] = create.select(JWD.CONCEPT).from(JWD).where(JWD.NAME.like(word)).fetch().toArray.toList.map {
          case r: Record =>ConceptId(r.getValue(JWD.CONCEPT).toInt)
        }
        cache += (word -> results)
        results
    }
  }
}
