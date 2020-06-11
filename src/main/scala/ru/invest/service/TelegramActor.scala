package ru.invest.service
import akka.actor.{Actor, ActorRef, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.stream.{KillSwitches, SharedKillSwitch}
import org.telegram.telegrambots.bots.DefaultBotOptions
import ru.core.bot.InvestInfoBot
import ru.tinkoff.invest.openapi.OpenApi

class TelegramActor(token: String, name: String, defaultBotOptions: DefaultBotOptions, chat_id: Long)(api: OpenApi)
    extends Actor {
  private val log: LoggingAdapter = Logging(context.system, this)

  private val investInfoBot: InvestInfoBot       = new InvestInfoBot(token, name, defaultBotOptions, chat_id, context.self)
  private var sharedKillSwitch: SharedKillSwitch = KillSwitches.shared("my-kill-switch")
  private var analysisFlag                       = false

  def receive: Receive = {
    case "Сбор аналитики"            => 23
    case "Остановка сбора аналитики" => 23

    case "Сбор аналитики"  =>
      if (analysisFlag) {
        investInfoBot.sendMessage("Сбор аналитики уже запущен")
      } else {
        analysisFlag = true
        sharedKillSwitch = KillSwitches.shared("my-kill-switch")
//        businessProcessServiceImpl.startAnalyticsJob(sharedKillSwitch).runAsyncAndForget(schedulerDB)
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

    case error                       => log.error("ERROR_Receive=" + error.toString)
  }
}
