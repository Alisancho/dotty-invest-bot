package ru.invest.service
import ru.invest.core.analytics.HistoricalCandles._
import java.util.concurrent.CompletionStage
import com.typesafe.scalalogging.LazyLogging
import akka.NotUsed
import akka.stream.SharedKillSwitch
import akka.stream.scaladsl.{RunnableGraph, Sink, Source}
import monix.execution.schedulers.SchedulerService
import akka.stream.{Materializer, SharedKillSwitch}
import akka.stream.scaladsl.{RunnableGraph, Sink, Source}
import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import monix.execution.schedulers.SchedulerService
import ru.tinkoff.invest.openapi.OpenApi
import akka.util.ccompat.JavaConverters._
import ru.tinkoff.invest.openapi.models.market.Instrument
import cats.implicits._
import akka.util.ccompat.JavaConverters._
import scala.concurrent.Future
import scala.language.postfixOps
import scala.concurrent.duration._
import scala.concurrent.java8.FuturesConvertersImpl.{CF, P}

object AnalyticTask extends LazyLogging{
  import ru.invest.core.analytics.CompletionStageCandles._
  import ru.invest.core.analytics.InstrumentCandles._
  def startAnalyticsJob(openApi: OpenApi)
                       (sharedKillSwitch: SharedKillSwitch)
                       (f: String => Task[_])
                       (sheduler:SchedulerService,materializer:Materializer): Task[Unit] =
    for {
      c    <- Task.fromFuture(openApi.getMarketContext.getMarketStocks.toScalaFuture)
      list = c.instruments.asScala.toList
      _    = analyticsStream(list, sharedKillSwitch)(openApi)(f)(sheduler).run()(materializer)
    } yield ()

  def analyticsStream(list: List[Instrument], sharedKillSwitch: SharedKillSwitch)(openApi: OpenApi)(f: String => Task[_])(
      sheduler: SchedulerService): RunnableGraph[NotUsed] =
    Source(list)
      .throttle(1, 800.millis)
      .via(sharedKillSwitch.flow)
      .map(instrument => {
        instrument.toTaskAnslytics(openApi)
          .runAsync {
            case Left(value) => println(value.getMessage)
            case Right(value) => {
              val list = value.get()
              println(list.toString)
              list
                .toAbsorption(f)(instrument)(sheduler)
                .onErrorHandle(p => println(p.getMessage))
                .runAsyncAndForget(sheduler)
              list.toHammer(f)(instrument)(sheduler).onErrorHandle(p => println(p.getMessage)).runAsyncAndForget(sheduler)
              list.toHarami(f)(instrument)(sheduler).onErrorHandle(p => println(p.getMessage)).runAsyncAndForget(sheduler)
            }
          }(sheduler)
      })
      .to(Sink.ignore)
}
