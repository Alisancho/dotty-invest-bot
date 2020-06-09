package ru.invest

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import cats.effect.ExitCode
import monix.eval.{Task, TaskApp}
import monix.execution.Scheduler
import monix.execution.schedulers.SchedulerService

import scala.concurrent.ExecutionContextExecutor
object Main extends TaskApp {
  implicit val system: ActorSystem                = ActorSystem()
  implicit val ec: ExecutionContextExecutor       = system.dispatcher
  implicit val materialiver: Materializer         = ActorMaterializer()
  implicit val schedulerTinkoff: SchedulerService = Scheduler.fixedPool(name = "my-fixed-tinkoff", poolSize = 5)
  override def run(args: List[String]): Task[ExitCode] =
    for {
      _ <- Task.unit
    } yield ExitCode.Success
}
