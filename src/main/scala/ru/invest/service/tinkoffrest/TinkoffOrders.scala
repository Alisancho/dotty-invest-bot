package ru.invest.service.tinkoffrest

import monix.eval.Task
import ru.tinkoff.invest.openapi.models.orders.{LimitOrder, MarketOrder, PlacedOrder}

trait TinkoffOrders extends Tinkoff {
  val accountId: String

  /**
    * Размещение лимитной заявки.
    *
    * @param figi            Идентификатор инструмента.
    * @param limitOrder      Параметры отправляемой заявки.
    * @return Размещённая заявка.
    */
  def limitOrders(figi: String, limitOrder: LimitOrder): Task[PlacedOrder] = Task.fromFuture {
    toScala(api.getOrdersContext.placeLimitOrder(figi, limitOrder, accountId))
  }

  /**
    * Размещение рыночной заявки.
    *
    * @param figi            Идентификатор инструмента.
    * @param marketOrder     Параметры отправляемой заявки.
    * @return Размещённая заявка.
    */
  def marketOrders(figi: String, marketOrder: MarketOrder): Task[PlacedOrder] = Task.fromFuture {
    toScala(api.getOrdersContext.placeMarketOrder(figi, marketOrder, accountId))
  }
}