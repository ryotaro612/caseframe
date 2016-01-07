package jp.ac.titech.cs.se.nakamura.cfgen.dao

import mockit.Deencapsulation
import scala.collection.mutable
import org.specs2.mutable.After
import org.specs2.Specification


/**
 * Memo ケースクラスがケースクラスを継承することはよくないらしい
 */
class CpcDaoSpec extends Specification {
  def is = s2""" $sequential
    上位下位概念の検索クエリのテスト
      下位概念を正しく検索でき，結果をキャッシュできているか ${DBTest().test1}
      上位概念を正しく検索できているか ${DBTest().test2}
      親子関係を正しく検索できるか ${DBTest().testCheckParentChild}
      共通の先祖を取得するテスト ${DBTest().testGetCommonAncestors}
      概念間距離を求めるテスト ${DBTest().testGetDistance}
  """
  case class DBTest() extends After {
    def after = {

    }

    def getCache(name: String): mutable.HashMap[ConceptId, List[ConceptId]]
      = Deencapsulation.getField(CpcDao, name)

    def test1 = this {
      val a = (ConceptId(1), 11245)
      CpcDao.getLowerConcepts(a._1).size must_== a._2

      val cashed: mutable.HashMap[ConceptId, List[ConceptId]] = getCache("lowerSelectedByUpper")
      cashed.get(a._1) must_!= None
    }
    def test2 = this {
      val a = (ConceptId(1), ConceptId(372529))
      CpcDao.getUpperConcepts(a._1)
      val upperCache: mutable.HashMap[ConceptId, List[ConceptId]] = getCache("upperSelectedByLower")
      upperCache(a._1)(0) must_==  a._2
    }

    def testCheckParentChild = this {
      // 43216 < 410344 < 372525
      CpcDao.isA(ConceptId(401344), ConceptId(43216)) must_== Some(Distance(1))
      CpcDao.isA(ConceptId(43216), ConceptId(401344)) must_== Some(Distance(1))
      CpcDao.isA(ConceptId(372525), ConceptId(43216)) must_== Some(Distance(2))
      CpcDao.isA(ConceptId(43216), ConceptId(372525)) must_== Some(Distance(2))
      CpcDao.isA(ConceptId(43216), ConceptId(43216)) must_== Some(Distance(0))
      CpcDao.isA(ConceptId(1), ConceptId(43216)) must_== None
    }
    def testGetCommonAncestors = this {
      /*
      43216 < 410344 < 372525 < 415261
      241151 < 372525
      410373 < 372524< 415261
      */
      CpcDao.getNearestCommonAncestors(ConceptId(4), ConceptId(2)) must_== List(ConceptId(372349))
      CpcDao.getNearestCommonAncestors(ConceptId(410373), ConceptId(43216)) must_== List(ConceptId(415261))
      CpcDao.getNearestCommonAncestors(ConceptId(43216), ConceptId(410373)) must_== List(ConceptId(415261))
      CpcDao.getNearestCommonAncestors( ConceptId(43216), ConceptId(415261)) must_== List(ConceptId(415261))
    }
    def testGetDistance = this {
      // Deencapsulation.invoke(CpcDao, "hoge") リフレクションのテスト

      CpcDao.getDistance(ConceptId(43216), ConceptId(410373)) must_== Distance(2)
      CpcDao.getDistance(ConceptId(372525), ConceptId(43216)) must_== Distance(3)
      CpcDao.getDistance( ConceptId(410373), ConceptId(43216)) must_== Distance(2)
      CpcDao.getDistance(ConceptId(43216), ConceptId(43216)) must_== Distance(0)
    }
  }


}

