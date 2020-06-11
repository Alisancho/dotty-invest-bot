package ru.invest.core.analytics

import java.util.concurrent.CompletionStage

import scala.concurrent.Future
import scala.concurrent.java8.FuturesConvertersImpl.{CF, P}

object CompletionStageCandles {
  def[T] (cs: CompletionStage[T]).toScalaFuture: Future[T] = {
    cs match {
      case cf: CF[T] => cf.wrapped
      case _ =>
        val p = new P[T](cs)
        cs whenComplete p
        p.future
    }
  }
}
