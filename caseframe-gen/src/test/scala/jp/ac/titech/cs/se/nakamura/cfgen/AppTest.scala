package jp.ac.titech.cs.se.nakamura.cfgen

import org.junit._
import Assert._
import com.google.inject.{AbstractModule, Guice, Injector, Inject}

@Test
class AppTest {

  @Test
  def testOK() = assertTrue(true)

  @Test
  def guiceName() {
    val injector: Injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected def configure() {
        bind(classOf[FruitGuice]).to(classOf[AppleGuice])
      }
    })

    val appTest: AppTest = injector.getInstance(classOf[AppTest])
    println(appTest.fruit.getName())
  }
  @Inject
  private val  fruit: FruitGuice = null



}

trait FruitGuice {
  protected val name: String

  def getName() = name
}
class AppleGuice extends FruitGuice {
  protected val name: String = "Apple"
}
class LemonGuice extends FruitGuice {
  protected val name: String = "Lemmon"
}
