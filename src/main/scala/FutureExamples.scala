import scala.concurrent.{ExecutionContext, Future}

object FutureExamples {
  def elapsed[T](x: => T): (Double, T) = {
    val initial = System.nanoTime()
    val result = x
    ((System.nanoTime() - initial) / 1000000000.0, result)
  }

  def doComputation(n: Int)(implicit ex: ExecutionContext): Future[Int] = {
    Future {
      Thread.sleep(1000)
      val data = n * 2
      println(s"value is  ${data}")
      data
    }
  }
}
