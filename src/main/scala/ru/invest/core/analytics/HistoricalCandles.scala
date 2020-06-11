package ru.invest.core.analytics

import monix.eval.Task
import monix.execution.schedulers.SchedulerService
import ru.invest.core.analytics.pattern.{Absorption, Hammer, Harami}

import ru.tinkoff.invest.openapi.models.market.{HistoricalCandles, Instrument}
object HistoricalCandles extends Absorption with Hammer with Harami{
  def (k: HistoricalCandles).toAbsorption(f: String => Task[_])(instrument: Instrument) (schedulerDB: SchedulerService): Task[_]= absorption(k)(instrument)(f)(schedulerDB)
  def (k: HistoricalCandles).toHammer(f: String => Task[_])(instrument: Instrument) (schedulerDB: SchedulerService): Task[_]= hammer(k)(instrument)(f)(schedulerDB)
  def (k: HistoricalCandles).toHarami(f: String => Task[_])(instrument: Instrument) (schedulerDB: SchedulerService): Task[_]= harami(k)(instrument)(f)(schedulerDB)
}