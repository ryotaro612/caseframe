package jp.ac.titech.cs.se.nakamura.cfgen.dao

import org.specs2.Specification
import mockit.Deencapsulation
import scala.collection.mutable
import jp.ac.titech.cs.se.nakamura.cfgen.dto.edr.CaseFrame

class JcpDaoSpec extends  Specification {
  def is = s2""" $sequential
              概念IDで問合せ，適当な格スロットの情報を取得取得できるか確かめる $test1
  """

  def test1 = {
    // 219692: 讃える
    val c = ConceptId(219692)
    val cf = JcpDao.getCaseFrameList(c)

    cf.size must_== 1
    cf.head.actName must_== "賛"
    val cache: mutable.HashMap[ConceptId, List[CaseFrame]]= Deencapsulation.getField(JcpDao, "cache")
    cache.size must_== 1
    cache contains (c) must_== true
  }
}
