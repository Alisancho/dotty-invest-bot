package ru.invest.core.analytics.pattern

import monix.eval.Task
import monix.execution.schedulers.SchedulerService
import ru.invest.core.analytics.СandleCandles._
import ru.tinkoff.invest.openapi.models.market.HistoricalCandles

trait Doge {
  def doge(l: HistoricalCandles)(f: AnalyticsTbl => Task[_])(schedulerDB: SchedulerService): Task[_] =
    for {
      _ <- Task.unit
    } yield ()
}
