import FutureExamples.doComputation
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.util.Success

class FutureExamplesSpec extends AnyFlatSpec with Matchers {
  private implicit val ex: ExecutionContextExecutor = global

  it should " example 1" in {

    val future = doComputation(1)

    future.value shouldBe None // Future not completed
    Thread.sleep(1010)
    future.value shouldBe Some(Success(2)) //Future completed
  }

  it should " Await" in {

    val future = doComputation(1)


    Await.result(future, Duration.Inf) shouldBe (2) //Future completed
  }

  it should "handle multiple futures using flatmap chaining" in {
    val future = doComputation(1) // 1 -> 2
      .flatMap(doComputation) // 2 -> 4    will run on same thread as first doComputation or different thread
      .flatMap(doComputation) // 4 -> 8
      .flatMap(doComputation) // 8 -> 16

    Await.result(future, Duration.Inf) shouldBe 16
  }

  it should "handle multiple futures using flatmap nested way" in {
    val future =
      doComputation(1).flatMap { a =>
        doComputation(a).flatMap { b =>
          doComputation(b).flatMap { c =>
            doComputation(c).map { d =>
              a + b + c + d //2+4+8+16
            }
          }
        }
      }
    Await.result(future, Duration.Inf) shouldBe 30
  }

  it should "handle multiple futures using flatmap using for comprehension" in {
    val future =
      for {
        a <- doComputation(1)
        b <- doComputation(a)
        c <- doComputation(b)
        d <- doComputation(c)
      } yield (a + b + c + d)

    Await.result(future, Duration.Inf) shouldBe 30
  }

}
