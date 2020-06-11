package ru.invest.service

import java.util.concurrent.CompletionStage

import akka.NotUsed
import akka.stream.SharedKillSwitch
import akka.stream.scaladsl.{RunnableGraph, Sink, Source}
import monix.eval.Task
import monix.execution.schedulers.SchedulerService
import ru.tinkoff.invest.openapi.models.market.Instrument
import akka.stream.{Materializer, SharedKillSwitch}
import akka.stream.scaladsl.{RunnableGraph, Sink, Source}
import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import monix.execution.schedulers.SchedulerService
import ru.tinkoff.invest.openapi.OpenApi
import akka.util.ccompat.JavaConverters._
import ru.tinkoff.invest.openapi.models.market.Instrument
import cats.implicits._

import scala.concurrent.Future
import scala.language.postfixOps
import scala.concurrent.duration._
import scala.concurrent.java8.FuturesConvertersImpl.{CF, P}
object AnalyticTask {
  
  def startAnalyticsJob(openApi: OpenApi)(sharedKillSwitch: SharedKillSwitch)(f: String => Task[_]): Task[Unit] =
    for {
      c    <- Task.fromFuture(toFuture(openApi.getMarketContext.getMarketStocks))
      list = c.instruments.asScala.toList
      _    = analyticsStream(list, sharedKillSwitch)(f)(sheduler).run()(materializer)
    } yield ()

  def analyticsStream(list: List[Instrument], sharedKillSwitch: SharedKillSwitch)(f: String => Task[_])(
      sheduler: SchedulerService): RunnableGraph[NotUsed] =
    Source(list)
      .throttle(1, 800.millis)
      .via(sharedKillSwitch.flow)
      .map(instrument => {
        tinkoff
          .getMarketCandles(instrument.figi)
          .runAsync {
            case Left(value) => logger.error(value.getMessage)
            case Right(value) => {
              val list = value.get()
              logger.info(list.toString)
              list
                .toAbsorption(f)(instrument)(sheduler)
                .onErrorHandle(p => logger.error(p.getMessage))
                .runAsyncAndForget(sheduler)
              list.toHammer(f)(instrument)(sheduler).onErrorHandle(p => logger.error(p.getMessage)).runAsyncAndForget(sheduler)
              list.toHarami(f)(instrument)(sheduler).onErrorHandle(p => logger.error(p.getMessage)).runAsyncAndForget(sheduler)
            }
          }(sheduler)
      })
      .to(Sink.ignore)
  
  def toFuture (cs: CompletionStage[T]): Future[T] = {
    cs match {
      case cf: CF[T] => cf.wrapped
      case _ =>
        val p = new P[T](cs)
        cs whenComplete p
        p.future
    }
  }
  
}
