package ru.invest.core.analytics
import java.time.OffsetDateTime
import java.util.Optional

import akka.stream.SharedKillSwitch
import ru.tinkoff.invest.openapi.OpenApi
import ru.tinkoff.invest.openapi.models.market.{CandleInterval, HistoricalCandles, Instrument}
import monix.eval.Task
import akka.util.ccompat.JavaConverters._

object InstrumentCandles {
  import ru.invest.core.analytics.CompletionStageCandles._
  def (instrument:Instrument).toTaskAnslytics(openApi:OpenApi):Task[Optional[HistoricalCandles]]=Task.fromFuture {
      openApi.getMarketContext
        .getMarketCandles(
          instrument.figi,
          OffsetDateTime.now().minusDays(10),
          OffsetDateTime.now().minusDays(1),
          CandleInterval.DAY).toScalaFuture
  }
  def (instrument:Instrument).toStringTelegramUp: String =
    s"""
       |Возможен рост актива:
       |FIGI = ${instrument.figi}
       |NAME = ${instrument.name}
       |TICKER = ${instrument.ticker}
       |URL = https://static.tinkoff.ru/brands/traiding/${instrument.isin}x160.png
       |""".stripMargin
  def (instrument:Instrument).toStringTelegramDown: String =
    s"""
       |Возможно падение актива:
       |FIGI = ${instrument.figi}
       |NAME = ${instrument.name}
       |TICKER = ${instrument.ticker}
       |URL = https://static.tinkoff.ru/brands/traiding/${instrument.isin}x160.png
       |""".stripMargin
}
