import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext.global
import scala.concurrent.ExecutionContextExecutor
import scala.util.Success

class FutureExamplesSpec extends AnyFlatSpec with Matchers {
  private implicit val ex: ExecutionContextExecutor = global

  it should " example 1" in {

    val f1 = FutureExamples.doComputation(1)

    f1.value shouldBe None // Future not completed
    Thread.sleep(1010)
    f1.value shouldBe Some(Success(1)) //Future completed

  }

}
