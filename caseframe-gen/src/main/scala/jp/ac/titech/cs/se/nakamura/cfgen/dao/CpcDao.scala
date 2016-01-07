package jp.ac.titech.cs.se.nakamura.cfgen.dao

import org.jooq.{TableField, Record1, Result, Record}
import jp.ac.titech.cs.se.nakamura.cfgen.dao.tables.records.CpcRecord

object CpcDao {

  import DicDBConnector.create
  import jp.ac.titech.cs.se.nakamura.cfgen.dao.tables.Cpc.CPC

  import scala.collection.mutable
  
  private val lowerSelectedByUpper = mutable.HashMap[ConceptId, List[ConceptId]]()
  def getLowerConcepts(upper: ConceptId): List[ConceptId] = {
    query(upper, lowerSelectedByUpper, CPC.LOWER_CONCEPT, CPC.UPPER_CONCEPT)
  }

  private val upperSelectedByLower = mutable.HashMap[ConceptId, List[ConceptId]]()
  def getUpperConcepts(lower: ConceptId): List[ConceptId] = {
    query(lower, upperSelectedByLower, CPC.UPPER_CONCEPT, CPC.LOWER_CONCEPT)
  }

  private def query(queryId: ConceptId, cached: mutable.HashMap[ConceptId, List[ConceptId]],
                    tableField: TableField[CpcRecord, Integer], tableField2: TableField[CpcRecord, Integer]) = {
    cached get queryId match {
      case Some(ids) => ids
      case None =>
        val result: Result[Record] = (create select tableField from CPC where (tableField2 eq queryId.id))
          .fetch()

        val queried: List[ConceptId] =  result.toArray.toList map {
          case i: Record =>
            ConceptId(i.getValue(tableField).toInt)
        }
        cached += (queryId -> queried)
        queried
    }
  }

  /**
   * @param parent 親概念
   * @param child 子概念
   * @return isA関係になければNone。それ以外では概念間距離を返す
   */
  def isA (parent: ConceptId, child: ConceptId): Option[Distance] = {
    val queue: mutable.Queue[(ConceptId, Distance)] = new mutable.Queue()
    queue.enqueue((child, Distance(0)))

    while(queue.nonEmpty) {
      val elm = queue dequeue()
      if(elm._1 == parent) {
        return Some(elm._2)
      }
      getUpperConcepts(elm._1) foreach(c => queue enqueue((c, Distance(elm._2.dst + 1))))
    }
    None
  }

  /**
   * @param c1 返り値の子孫
   * @param c2 返り値の子孫
   * @return c1とc2の概念の最も若い共通の概念のリストを返す
   */
  def getNearestCommonAncestors(c1: ConceptId, c2: ConceptId): List[ConceptId] = {
    def getNearestAncestors(cons: List[ConceptId], descendant: ConceptId): List[ConceptId]  = {
      cons filter (c => isA(c, descendant) != None) match {
        case Nil => getNearestAncestors((cons map (cc => getUpperConcepts(cc))).flatten, descendant)
        case ans => ans
      }
    }
    getNearestAncestors(List(c1), c2)
  }

  /**
   * 概念間距離を返す
   * @return 互いが親子関係であれば世代差を返す。それ以外では，共通の親と一方の概念の世代差を返す。
   *         返す値が小さくなる方の引数の概念と共通の親を比較する
   */
  def getDistance(c1: ConceptId, c2: ConceptId): Distance = {
    isA(c1, c2) match {
      case Some(dst) => dst
      case None => isA(c2, c1) match {
        case Some(dst) => dst
        case None =>
          val ances = getNearestCommonAncestors(c1, c2)
          Distance(math.min(isA(ances.head, c1).get.dst, isA(ances.head, c2).get.dst))
      }
    }
  }

  implicit private def jooqTypeConv(r: Result[Record1[Integer]]): Result[Record] = r.asInstanceOf[Result[Record]]

}

case class ConceptId(id: Int)
case class DicDBException(msg: String)
case class Distance(dst: Int)