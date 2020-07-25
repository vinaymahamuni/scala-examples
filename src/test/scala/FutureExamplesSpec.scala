import FutureExamples.{doComputation, elapsed}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
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

  it should "use for-yield block to perform sequential computations in the future." in {
    val future =
      for {
        a <- doComputation(1)
        b <- doComputation(a)
        c <- doComputation(b)
        d <- doComputation(c)
      } yield (a + b + c + d)


    val (elapsedTime, _) = elapsed {
      Await.result(future, Duration.Inf) shouldBe 30
    }

    elapsedTime shouldEqual 4.0 +- 0.1

  }
  it should "this example wont do parallel execution" in {
    val future: Future[Int] =
      for {
        a <- doComputation(1)
        b <- doComputation(2)
        c <- doComputation(4)
        d <- doComputation(8)
      } yield (a + b + c + d)

    val (elapsedTime, _) = elapsed {
      Await.result(future, Duration.Inf) shouldBe 30
    }

    elapsedTime shouldEqual 4.0 +- 0.1 // wont be 1 second
  }

  it should "use for-yield block to perform parallel computations in the future. ex1" in {
    val eventualInt = doComputation(1)
    val eventualInt1 = doComputation(2)
    val eventualInt2 = doComputation(4)
    val eventualInt3 = doComputation(8)

    val future: Future[Int] =
      for {
        a <- eventualInt
        b <- eventualInt1
        c <- eventualInt2
        d <- eventualInt3
      } yield (a + b + c + d)

    val (elapsedTime, _) = elapsed {
      Await.result(future, Duration.Inf) shouldBe 30
    }

    elapsedTime shouldEqual 1.0 +- 0.1
  }


  it should "use for-yield block to perform parallel computations in the future. ex2" in {
    // All futures are created up front and started in parallel.
    // The entire computation takes about 1 second.
    val futures: Seq[Future[Int]] = Seq(10, 20, 30, 40).map(doComputation)

    val futureSum: Future[Int] = for {
      a <- futures(0)
      b <- futures(1)
      c <- futures(2)
      d <- futures(3)
    } yield {
      a + b + c + d
    }
    val (elapsedTime, _) = elapsed {
      // The `result` is still in the future, need to wait for it.
      Await.result(futureSum, Duration.Inf) shouldEqual 200
    }
    elapsedTime shouldEqual 1.0 +- 0.1
  }


  it should "perform parallel computations and wait for all to finish" in {
    // This starts all the Future values in parallel.
    // The computation takes about 1 second.
    val result1: Seq[Future[Int]] = Seq(10, 20, 30, 40).map { n ⇒ doComputation(n) }
    val result2: Future[Seq[Int]] = Future.sequence(result1)

    val sum: Future[Int] = result2.map((s: Seq[Int]) ⇒ s.sum)

    val (elapsedTime, _) = elapsed {
      Await.result(sum, Duration.Inf) shouldEqual 200
    }
    elapsedTime shouldEqual 1.0 +- 0.1
  }
}
