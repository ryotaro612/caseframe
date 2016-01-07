package jp.ac.titech.cs.se.nakamura.cfgen.dto.edr

import play.api.libs.json._
import jp.ac.titech.cs.se.nakamura.cfgen.dao.ConceptId
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber

case class CaseFrame(id: ConceptId, actName: String, info: String){
  val slotInfo: List[CaseSlot] = Json.parse(info) match {
    case obj: JsObject =>
      obj.fieldSet.map(f => CaseSlot(f._1, f._2)).toList

    case _ => throw new RuntimeException(info)
  }

}

abstract class CaseSlot {
  val info: JsValue
  def parseFormula(v: JsValue): Formula =  v match {
    case n: JsNumber => AtomicFormula(ConceptId(n.as[Int]))
    case a: JsArray =>

      a(0) match {
      case and: JsString if and.value == "&"
        => AndFormula(a.value.drop(1).map(f => parseFormula(f)).toList)
      case or: JsString if or.value == "|"
        => OrFormula(a.value.drop(1).map(f => parseFormula(f)).toList)
      case not: JsString if not.value == "!"
        => NotFormula(parseFormula(a(1)))
    }
    case _ => throw new RuntimeException(v.toString())
  }
  val (surfaceCase, formula, caption) = info match {
    case info: JsArray =>
      (info(0).as[String], parseFormula(info(1)), info(2).as[String])
    case _ => throw new RuntimeException(info.toString())
  }
}
object CaseSlot {
  def apply(deepCase: String, info: JsValue)  = deepCase match  {
    case "agent" => Agent(info)
    case "object" => Object(info)
    case "place" => Place(info)
    case "goal" => Goal(info)
    case "implement" => Implement(info)
    case "source" => Source(info)
    case "condition" => Condition(info)
    case _ => throw new RuntimeException(deepCase)
  }
}

case class Agent(info: JsValue) extends CaseSlot
case class Object(info: JsValue) extends CaseSlot
case class Place(info: JsValue) extends CaseSlot
case class Goal(info: JsValue) extends CaseSlot
case class Implement(info: JsValue) extends CaseSlot
case class Source(info: JsValue) extends CaseSlot
case class Condition(info: JsValue) extends CaseSlot


abstract class Formula
case class AtomicFormula(id: ConceptId) extends Formula
case class OrFormula(formulas:  List[Formula]) extends Formula
case class AndFormula(formulas: List[Formula]) extends Formula
case class NotFormula(formula: Formula) extends Formula