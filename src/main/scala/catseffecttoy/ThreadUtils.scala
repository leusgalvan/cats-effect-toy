package catseffecttoy

import cats._
import cats.implicits._
import scala.collection.JavaConverters.asScalaSet
object ThreadUtils {
  object Instances {
    implicit val threadPaddedShow = Show.show[Thread] { thread =>
      f"[${thread.getName}%50s | ${thread.getState}%12s]"
    }
    implicit val threadNameShow = Show.show[Thread] { _.getName }
  }

  def showAllThreads(): String = {
    import Instances.threadPaddedShow
    asScalaSet(Thread.getAllStackTraces.keySet).toSeq
      .map(_.show)
      .sorted
      .mkString("[\n", "\n", "\n]")
  }

  def showCurrentThread(): String = {
    import Instances.threadNameShow
    Thread.currentThread().show
  }

}
