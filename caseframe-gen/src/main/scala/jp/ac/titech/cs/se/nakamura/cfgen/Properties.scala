package jp.ac.titech.cs.se.nakamura.cfgen

import java.nio.file.Paths


object Properties {
  private val p = new java.util.Properties()
    p.load(getClass.getResourceAsStream("/caseframe-gen.properties"))

  def apply(key: String): Option[String] = {
    p.getProperty(key) match {
      case null => None
      case v: String => Some(v)
    }
  }

}
