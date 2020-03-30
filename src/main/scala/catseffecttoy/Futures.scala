package catseffecttoy

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.global
import scala.concurrent.duration.Duration

object Futures {
  def computeFactorial(n: Int): Int = {
    val promise = Promise[Int]()
    val runnable: Runnable = () => {
      if (n >= 0) {
        val result = (1 to n).product
        promise.success(result)
      } else {
        promise.failure(
          new IllegalArgumentException(s"n can't be negative: $n")
        )
      }
    }
    global.execute(runnable)
    val future = promise.future
    Await.result(future, Duration.Inf)
  }

  def createFuture[A](body: => A): Future[A] = {
    val promise = Promise[A]()
    val runnable: Runnable = () => {
      try {
        promise.success(body)
      } catch {
        case ex: Throwable => promise.failure(ex)
      }
    }
    global.execute(runnable)
    promise.asInstanceOf[Future[A]]
  }
}
