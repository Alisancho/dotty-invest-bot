package ru.invest.service
import akka.actor.{Actor, ActorRef, Props, SupervisorStrategy}
import akka.event.{Logging, LoggingAdapter}
import akka.stream.{KillSwitches, Materializer, SharedKillSwitch}
import monix.execution.schedulers.SchedulerService
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.bots.DefaultBotOptions
import ru.core.bot.InvestInfoBot
import ru.tinkoff.invest.openapi.OpenApi
import monix.eval.Task
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy._
import scala.concurrent.duration._
import org.telegram.telegrambots.meta.{ApiContext, TelegramBotsApi}

object TelegramActor {
  def apply(token: String,
            name: String,
            chat_id: Long,
            api: OpenApi,
            schedulerTinkoff: SchedulerService,
            materializer: Materializer): Props =
    Props(new TelegramActor(token, name, chat_id, api, schedulerTinkoff, materializer))
  ApiContextInitializer.init()
}

class TelegramActor(token: String,
                    name: String,
                    chat_id: Long,
                    api: OpenApi,
                    schedulerTinkoff: SchedulerService,
                    materializer: Materializer)
    extends Actor {
 import TelegramActor._

  private val log: LoggingAdapter = Logging(context.system, this)

  private val telegramBotsApi:TelegramBotsApi = new TelegramBotsApi()
  private lazy val investBot: InvestInfoBot = new InvestInfoBot(token, name, chat_id, context.self)
  telegramBotsApi.registerBot(investBot)

  private var sharedKillSwitch: SharedKillSwitch = KillSwitches.shared("my-kill-switch")
  private var analysisFlag                       = false

  def receive: Receive = {
    case "Сбор аналитики" =>{
      log.info("Сбор аналитики")
      if (analysisFlag) {
        investBot.sendMessage("Сбор аналитики уже запущен")
      } else {
        analysisFlag = true
        sharedKillSwitch = KillSwitches.shared("my-kill-switch")
        AnalyticTask.startAnalyticsJob(api)(sharedKillSwitch)(i => Task {
          investBot.sendMessage(i)
        })(schedulerTinkoff,
          materializer).runAsyncAndForget(schedulerTinkoff)
        investBot.sendMessage("Успешный запуск сбора аналитики")
      }
      }
    case "Остановка сбора аналитики" => {
      log.info("Остановка сбора аналитики")
      if (analysisFlag) {
        sharedKillSwitch.shutdown()
        analysisFlag = false
        investBot.sendMessage("Сбор аналитики остановлен")
      } else {
        investBot.sendMessage("Сбор аналитики не запущен")
      }
    }
    case error => log.error("ERROR_Receive=" + error.toString)
  }
  private def getInvestInfoBot(defaultBotOptions: Option[DefaultBotOptions]): InvestInfoBot =
    (for {
      z <- defaultBotOptions
    } yield new InvestInfoBot(token, name, z, chat_id, context.self))
      .getOrElse(new InvestInfoBot(token, name, chat_id, context.self))
  

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1.second){
      case e: Exception => {
        log.error("ERROR_IN_TELEGRAM_ACTOR=" + e.getMessage)
        Stop
      }
    }
}
