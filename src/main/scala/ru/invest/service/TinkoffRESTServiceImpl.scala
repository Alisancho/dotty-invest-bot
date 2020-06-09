package ru.invest.service

import com.typesafe.scalalogging.LazyLogging
import ru.tinkoff.invest.openapi.OpenApi
import ru.invest.service.tinkoffrest._

class TinkoffRESTServiceImpl(val api: OpenApi, val accountId: String)
  extends TinkoffMarket with TinkoffOrders with TinkoffPortfolio with LazyLogging {
}
