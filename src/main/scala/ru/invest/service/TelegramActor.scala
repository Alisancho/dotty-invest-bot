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
import ru.invest.core.ConfigObject._
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

  private lazy val telegramBotsApi:TelegramBotsApi = new TelegramBotsApi()
  
  private lazy val investBot: InvestInfoBot = new InvestInfoBot(token, name, chat_id, context.self)
  telegramBotsApi.registerBot(investBot)

  private var sharedKillSwitch_up: SharedKillSwitch = KillSwitches.shared("my-kill-switch-up")
  private var analysisFlag_up                       = false

  private var sharedKillSwitch_down: SharedKillSwitch = KillSwitches.shared("my-kill-switch-down")
  private var analysisFlag_down                       = false

  def receive: Receive = {
    case ANALYTICS_UP =>{
      if (analysisFlag_up) {
        investBot.sendMessage("Сбор аналитики уже запущен")
      } else {
        analysisFlag_up = true
        sharedKillSwitch_up = KillSwitches.shared("my-kill-switch")
        AnalyticTask.startAnalyticsJobUp(api)(sharedKillSwitch_up)(i => Task {
          investBot.sendMessage(i)
        })(schedulerTinkoff,
          materializer).runAsyncAndForget(schedulerTinkoff)
        investBot.sendMessage("Успешный запуск сбора аналитики")
      }
      }
    case ANALYTICS_DOWN =>{
      if (analysisFlag_down) {
        investBot.sendMessage("Сбор аналитики уже запущен")
      } else {
        analysisFlag_down = true
        sharedKillSwitch_down = KillSwitches.shared("my-kill-switch")
        AnalyticTask.startAnalyticsJobDown(api)(sharedKillSwitch_down)(i => Task {
          investBot.sendMessage(i)
        })(schedulerTinkoff,
          materializer).runAsyncAndForget(schedulerTinkoff)
        investBot.sendMessage("Успешный запуск сбора аналитики")
      }
    }
    case ANALYTICS_STOP_UP => {
      if (analysisFlag_up) {
        sharedKillSwitch_up.shutdown()
        analysisFlag_up = false
        investBot.sendMessage("Сбор аналитики остановлен")
      } else {
        investBot.sendMessage("Сбор аналитики не запущен")
      }
    }
    case ANALYTICS_STOP_DOWN => {
      if (analysisFlag_down) {
        sharedKillSwitch_down.shutdown()
        analysisFlag_down = false
        investBot.sendMessage("Сбор аналитики остановлен")
      } else {
        investBot.sendMessage("Сбор аналитики не запущен")
      }
    }
    case error => log.error("ERROR_Receive=" + error.toString)
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1.second){
      case e: Exception => {
        log.error("ERROR_IN_TELEGRAM_ACTOR=" + e.getMessage)
        Stop
      }
    }
}
