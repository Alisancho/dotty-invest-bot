package ru.invest.service
import akka.NotUsed
import akka.stream.scaladsl.{RunnableGraph, Sink, Source}
import com.typesafe.scalalogging.LazyLogging
import monix.catnap.MVar
import monix.eval.Task
import ru.tinkoff.invest.openapi.OpenApi
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent

class MonitoringService {
  def startDayTrading(figi: String): Task[_] =
    for {
      _ <- Task.unit
    } yield ()
  
  def mainStream(api: OpenApi): RunnableGraph[NotUsed] =
    Source
      .fromPublisher(api.getStreamingContext.getEventPublisher)
      .filter({
        case candle: StreamingEvent.Candle => {
          println(candle.toString)
          true
        }
        case orderbook: StreamingEvent.Orderbook => {
          println(orderbook.toString)
          false
        }
        case instrumentInfo: StreamingEvent.InstrumentInfo => {
          println(instrumentInfo.toString)
          false
        }
        case error: StreamingEvent.Error => {
          println(error.getError)
          false
        }
      })
      .mapAsync(parallelism = 1)({
       // case candle: StreamingEvent.Candle => futureTask(mVar, telSer, candle)
      })
      .to(Sink.ignore)

}
