package ru.invest.service.tinkoffrest

import java.time.OffsetDateTime
import java.util.Optional

import monix.eval.Task
import ru.tinkoff.invest.openapi.models.market.{CandleInterval, HistoricalCandles, InstrumentsList}

trait TinkoffMarket extends Tinkoff {

  /**
    * Получение списка акций
    */
  def getMarketStocks: Task[InstrumentsList] = Task.fromFuture {
    toScala(api.getMarketContext.getMarketStocks)
  }

  /**
    * Получение списка ETF
    */
  def getMarketEtfs: Task[InstrumentsList] = Task.fromFuture {
    toScala(api.getMarketContext.getMarketEtfs)
  }

  /**
    * Получение списка облигаций
    */
  def getMarketBonds: Task[InstrumentsList] = Task.fromFuture {
    toScala(api.getMarketContext.getMarketBonds)
  }

  /**
    * Получение списка валютных пар
    */
  def getMarketCurrencies: Task[InstrumentsList] = Task.fromFuture {
    toScala(api.getMarketContext.getMarketCurrencies)
  }

  /**
    * Получение исторических свечей по FIGI
    */
  def getMarketCandles(figi: String): Task[Optional[HistoricalCandles]] = Task.fromFuture {
    toScala(
      api.getMarketContext
        .getMarketCandles(
          figi,
          OffsetDateTime.now().minusDays(10),
          OffsetDateTime.now().minusDays(1),
          CandleInterval.DAY))
  }

}
