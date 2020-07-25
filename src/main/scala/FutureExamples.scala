import scala.concurrent.{ExecutionContext, Future}

object FutureExamples {

  def doComputation(n: Int)(implicit ex: ExecutionContext): Future[Int] = {
    Future {
      Thread.sleep(1000)
      println(s"value is  ${n}")
      n
    }
  }
}
