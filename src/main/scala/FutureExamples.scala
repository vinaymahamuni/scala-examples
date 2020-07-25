import scala.concurrent.{ExecutionContext, Future}

object FutureExamples {

  def doComputation(n: Int)(implicit ex: ExecutionContext): Future[Int] = {
    Future {
      Thread.sleep(1000)
      val data = n * 2
      println(s"value is  ${data}")
      data
    }
  }
}
