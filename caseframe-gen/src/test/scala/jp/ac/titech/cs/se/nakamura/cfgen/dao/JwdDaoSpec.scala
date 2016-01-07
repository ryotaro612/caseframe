package jp.ac.titech.cs.se.nakamura.cfgen.dao

import org.specs2.Specification
import mockit.Deencapsulation
import scala.collection.mutable

class JwdDaoSpec extends Specification {
   def is = s2"""$sequential
               単語で概念を正しく問合せられるか $test
   """

  def test = {
    // 202960, 292951
    val word = "砂肝"
    val ids = JwdDao getConceptIds word

    ids.size must_== 2
    (ids contains ConceptId(202960)) && (ids contains ConceptId(292951)) must_== true

    val cache: mutable.HashMap[String, List[ConceptId]]= Deencapsulation.getField(JwdDao, "cache")
    cache.nonEmpty must_== true
  }
}
