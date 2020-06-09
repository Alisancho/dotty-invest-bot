package ru.invest.service.tinkoffrest

import monix.eval.Task
import ru.tinkoff.invest.openapi.models.portfolio.{Portfolio, PortfolioCurrencies}

trait TinkoffPortfolio extends Tinkoff {
  val accountId: String

  def getPortfolio: Task[Portfolio] = Task.fromFuture {
    toScala(api.getPortfolioContext.getPortfolio(accountId))
  }

  def getPortfolioCurrencies: Task[PortfolioCurrencies] = Task.fromFuture {
    toScala(api.getPortfolioContext.getPortfolioCurrencies(accountId))
  }
}