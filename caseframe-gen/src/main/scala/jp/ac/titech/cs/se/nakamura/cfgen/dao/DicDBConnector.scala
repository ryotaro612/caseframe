package jp.ac.titech.cs.se.nakamura.cfgen.dao

import java.nio.file.Paths
import java.sql.DriverManager
import org.jooq.{DSLContext, SQLDialect}
import org.jooq.impl.DSL

/**
 * TODO close メソッドの実装
 */
object DicDBConnector {

  private val user = "root"
  private val pass = ""
  private val url = "jdbc:sqlite:" + Paths.get(getClass.getResource("/edr.db").toURI)

  Class.forName("org.sqlite.JDBC").newInstance()
  private val conn = DriverManager.getConnection(url, user, pass)
  private val dslContext = DSL.using(conn, SQLDialect.SQLITE)

  def create: DSLContext = {
    if(conn.isClosed) {
      throw ClosedDicDBConnectionException(s"$url is closed.")
    }
     dslContext
  }

  case class ClosedDicDBConnectionException(msg: String) extends RuntimeException
}
