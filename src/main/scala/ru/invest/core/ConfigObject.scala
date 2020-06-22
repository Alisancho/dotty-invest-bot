package ru.invest.core

import com.typesafe.config.ConfigFactory

object ConfigObject {

  sealed trait Token

  private val conf = ConfigFactory.load()
  val TOKEN: String = conf.getString("tinkoff.mail")
  
  val SERVER_HOST: String = conf.getString("server.host")
  val SERVER_PORT: Int = conf.getInt("server.port")
  val TINKOFF_BROKER_ACCOUNT_ID: String = conf.getString("tinkoff.broker.account.id")

  val SCHEDULER_POOL_TINKOFF: Int = conf.getInt("scheduler.pool.tinkoff")

  val TELEGRAM_TOKEN: String = conf.getString("telegtam.token")
  val TELEGRAM_NAMEBOT: String = conf.getString("telegtam.namebot")
  val TELEGRAM_CHAT_ID: Long = conf.getInt("telegtam.chat.id")
  
  val ANALYTICS_UP:String = "Получить список возможного роста"
  val ANALYTICS_DOWN:String = "Получить список возможного падения"
  val ANALYTICS_STOP_UP: String = "Остановка сбора аналитики роста"
  val ANALYTICS_STOP_DOWN: String = "Остановка сбора аналитики падения"

}
