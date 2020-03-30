package catseffecttoy

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{Executors, ThreadFactory}

import scala.concurrent.{
  ExecutionContext,
  ExecutionContextExecutorService,
  Future
}

object Threads {
  def threadFactory(initial: Int): ThreadFactory = new ThreadFactory {
    var n = new AtomicInteger(initial)
    override def newThread(r: Runnable): Thread =
      new Thread(r, s"my-worker-${n.getAndIncrement()}")
  }

  def singleThreadExecutor =
    Executors.newSingleThreadExecutor(threadFactory(0))
  def singleThreadExecutionContext =
    ExecutionContext.fromExecutorService(singleThreadExecutor)

  def twoThreadsExecutor = Executors.newFixedThreadPool(2, threadFactory(0))
  def twoThreadsExecutionContext =
    ExecutionContext.fromExecutorService(twoThreadsExecutor)

  def cachedExecutor = Executors.newCachedThreadPool(threadFactory(1))
  def cachedExecutionContext =
    ExecutionContext.fromExecutorService(cachedExecutor)

  def workStealingExecutor = Executors.newWorkStealingPool()
  def workStealingExecutionContext =
    ExecutionContext.fromExecutorService(workStealingExecutor)

  def simplePrintTask(s: String): Runnable = () => {
    println(s"${ThreadUtils.showCurrentThread()}: $s")
  }

  def blocking(secondsToBlock: Int)(task: Runnable): Runnable = () => {
    Thread.sleep(secondsToBlock * 1000)
    task.run()
  }

  def runTasksInNewThread(tasks: Seq[Runnable]): Unit = {
    val myThreadFactory = threadFactory(0)
    tasks.foreach(task => {
      val workerThread = myThreadFactory.newThread(task)
      workerThread.start()
    })
    Future.successful(1).transform(x => x)(singleThreadExecutionContext)
  }

  def runTasksInExecutionContext(
    tasks: Seq[Runnable],
    executionContext: ExecutionContextExecutorService
  ): Unit = {
    tasks.foreach(executionContext.execute)
    executionContext.shutdown()
  }

  def runTasksInSeparateFutures(
    tasks: Seq[Runnable]
  )(implicit executionContext: ExecutionContextExecutorService): Unit = {
    tasks.foreach(task => Future(task.run()))
  }

  def runTasksInChainedFutures(
    tasks: Seq[Runnable]
  )(implicit executionContext: ExecutionContextExecutorService): Unit = {
    tasks.foldLeft(Future.successful(())) { (future, task) =>
      Future(task.run())(executionContext).flatMap(_ => future)
    }
  }

}
