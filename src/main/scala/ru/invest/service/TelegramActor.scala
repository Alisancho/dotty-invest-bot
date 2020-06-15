package ru.invest.service
import akka.actor.{Actor, ActorRef, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.stream.{KillSwitches, Materializer, SharedKillSwitch}
import monix.execution.schedulers.SchedulerService
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.ApiContext
import ru.core.bot.InvestInfoBot
import ru.tinkoff.invest.openapi.OpenApi
import monix.eval.Task
object TelegramActor {
  
  def apply(token: String,
            name: String,
            chat_id: Long,
            defaultBotOptions: Option[DefaultBotOptions],
            api: OpenApi,
            schedulerTinkoff: SchedulerService,
            materializer:Materializer): Props =
    Props(new TelegramActor(token, name, chat_id, defaultBotOptions, api, schedulerTinkoff,materializer))
}

class TelegramActor(token: String,
                    name: String,
                    chat_id: Long,
                    defaultBotOptions: Option[DefaultBotOptions],
                    api: OpenApi,
                    schedulerTinkoff: SchedulerService,
                    materializer:Materializer)
    extends Actor {
  ApiContextInitializer.init()
  private val log: LoggingAdapter = Logging(context.system, this)

  private val investInfoBot: InvestInfoBot = getInvestInfoBot(defaultBotOptions)

  private var sharedKillSwitch: SharedKillSwitch = KillSwitches.shared("my-kill-switch")
  private var analysisFlag                       = false

  def receive: Receive = {
    case "Сбор аналитики"            => if (analysisFlag) {
      investInfoBot.sendMessage("Сбор аналитики уже запущен")
    } else {
      analysisFlag = true
      sharedKillSwitch = KillSwitches.shared("my-kill-switch")

      investInfoBot.sendMessage("Успешный запуск сбора аналитики")
    }
    case "Остановка сбора аналитики" => if (analysisFlag) {
      sharedKillSwitch.shutdown()
      analysisFlag = false
      investInfoBot.sendMessage("Сбор аналитики остановлен")
    } else {
      investInfoBot.sendMessage("Сбор аналитики не запущен")
    }

    case "Сбор аналитики" =>
      if (analysisFlag) {
        investInfoBot.sendMessage("Сбор аналитики уже запущен")
      } else {
        analysisFlag = true
        sharedKillSwitch = KillSwitches.shared("my-kill-switch")
        AnalyticTask.startAnalyticsJob(api)(sharedKillSwitch)(i => Task{investInfoBot.sendMessage(i)})(schedulerTinkoff,materializer)

        investInfoBot.sendMessage("Успешный запуск сбора аналитики")
      }
    case "Остановка сбора аналитики" =>
      if (analysisFlag) {
        sharedKillSwitch.shutdown()
        analysisFlag = false
        investInfoBot.sendMessage("Сбор аналитики остановлен")
      } else {
        investInfoBot.sendMessage("Сбор аналитики не запущен")
      }

    case error => log.error("ERROR_Receive=" + error.toString)
  }
  private val getInvestInfoBot: Option[DefaultBotOptions] => InvestInfoBot = defaultBotOptions =>
    (for {
      z <- defaultBotOptions
    } yield new InvestInfoBot(token, name, z, chat_id, context.self))
      .getOrElse(new InvestInfoBot(token, name, chat_id, context.self))
}
