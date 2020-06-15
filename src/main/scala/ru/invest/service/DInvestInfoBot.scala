package ru.invest.service
import akka.actor.ActorRef
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

class DInvestInfoBot(token: String, 
                     name: String,
                     chat_id: Long, 
                     acctorRef: ActorRef) extends TelegramLongPollingBot {

  
  override def onUpdateReceived(update: Update): Unit = ???

  override def getBotUsername: String = ???

  override def getBotToken: String = ???
}
