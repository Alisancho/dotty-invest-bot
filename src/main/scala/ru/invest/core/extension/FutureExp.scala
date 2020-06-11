package ru.invest.core.extension
import java.util.concurrent.CompletionStage
import monix.eval.Task
import scala.concurrent.Future
import cats.kernel.Monoid
import scala.concurrent.java8.FuturesConvertersImpl.{CF, P}

object ws {
  given conversion2Task as Conversion[CompletionStage[T], Task[T]] = {
    def apply[T](cs: CompletionStage[T]) = Task.fromFuture {
      cs match {
        case cf: CF[T] => cf.wrapped
        case _ =>
          val p = new P[T](cs)
          cs whenComplete p
          p.future
      }
    }
  }
}