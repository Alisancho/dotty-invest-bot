package ru.invest

import java.util.logging.Logger

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.{ActorMaterializer, Materializer}
import cats.effect.ExitCode
import monix.eval.{Task, TaskApp}
import monix.execution.Scheduler
import monix.execution.schedulers.SchedulerService
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.ApiContext
import ru.tinkoff.invest.openapi.OpenApi
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApiFactory
import ru.invest.core.ConfigObject._

import scala.concurrent.ExecutionContextExecutor
import ru.invest.service.TelegramActor
object Main extends TaskApp {
  implicit val system: ActorSystem                = ActorSystem()
  implicit val ec: ExecutionContextExecutor       = system.dispatcher
  implicit val materializer: Materializer         = ActorMaterializer()
  implicit val schedulerTinkoff: SchedulerService = Scheduler.fixedPool(name = "my-fixed-tinkoff", poolSize = 5)

  override def run(args: List[String]): Task[ExitCode] =
    for {
      _  <- Task.unit
      ap <- apiTask
      ta = system.actorOf(
        TelegramActor(TELEGRAM_TOKEN, TELEGRAM_NAMEBOT, TELEGRAM_CHAT_ID, ap, schedulerTinkoff,materializer))
    } yield ExitCode.Success

  private val apiTask: Task[OpenApi] = for {
    log <- Task { Logger.getLogger("Pooo") }
    api <- Task { new OkHttpOpenApiFactory(TOKEN, log).createOpenApiClient(schedulerTinkoff) }
  } yield api
}
