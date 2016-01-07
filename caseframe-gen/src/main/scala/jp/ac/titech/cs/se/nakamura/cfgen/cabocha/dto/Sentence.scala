package jp.ac.titech.cs.se.nakamura.cfgen.cabocha.dto

import scala.collection.mutable.ArrayBuffer

/**
 * @param lines "cabocha -n1 -f1"が一文を解析した結果からEOSを除いた文字列
 */
case class Sentence(lines: List[String]) {
  val chunks = {
    val chunkLines = lines.foldLeft(new ArrayBuffer[ArrayBuffer[String]]())((stuff, line) => {
      line.startsWith("*") match {
        case true => stuff += (new ArrayBuffer[String]() += line)
        case false => stuff.last += line; stuff
      }
    }).toList.map(ab => ab.toList)

    val chns = chunkLines.map(tokenLines => Chunk(tokenLines, this))
    chns.foreach(ch => ch.inverseLink = chns.filter(c => c.link == ch.id))
    chns
  }

  /**
   * @return どの文節にも係っていない動詞を返す
   */
  def getIndepVerbs = chunks filterNot(c => c.isLinked) flatMap (c => c.getVerbs)

  def getChunksWithCaseParticle = chunks filter ( chunk => chunk.containsCaseParticle)
}

case class Chunk(lines: List[String], s: Sentence) {
  val sentence = s
  private var invLink: List[Chunk] = List()
  def inverseLink = invLink
  def inverseLink_=(chunks: List[Chunk])  {invLink = chunks}

  /**
   * TODO unlinkであることを-1で表現していることは分かりにくいので
   * linkはカプセル化したい
   */
  val (id, link, rel, head, func, score) =  {
    val analysis = lines(0) split " "
    val headFunc = analysis(3).split("/")
    (   analysis(1).toInt
      , analysis(2).substring(0, analysis(2).length - 1).toInt
      , analysis(2).substring(analysis(2).length - 1)
      , headFunc(0).toInt
      , headFunc(1).toInt
      , analysis(4).toDouble
    )
  }
  def isLinked = link != -1
  val tokens =  lines.drop(1).map(l => Token(l, this))

  def getVerbs = tokens filter( t => t.isVerb)

  def containsCaseParticle = tokens.count(token => token.isCaseParticle) > 0

  def containsUniCase (read: String) = tokens.count(token => token.isCaseParticle && token.surface == read) == 1

  def containsUniNoun = (tokens count (token => token.isNoun)) == 1

}

case class Token(line: String, parent: Chunk) {
  val chunk = parent

  /**
   * 表層形，品詞，品詞細分類1，品詞細分類2，品詞細分類3，活用形，活用型，原形，読み，発音
   */
  val (surface, pos, pos1, pos2, pos3, ctype, cform, base, read, pron, ne) = {
    val a = line split "\t"
    val feats = a(1) split ","
    (a(0), feats(0),feats(1),feats(2),feats(3),feats(4),feats(5),feats(6),feats(7), feats(8), a(2))
  }

  def isVerb = pos == "動詞"
  def isCaseParticle = pos1 == "格助詞"
  def isNoun = pos == "名詞"
}

