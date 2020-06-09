package ru.invest.service.tinkoffrest

import java.util.concurrent.{CompletionStage, Executors}

import ru.tinkoff.invest.openapi.OpenApi

import scala.concurrent.Future
import scala.concurrent.java8.FuturesConvertersImpl.{CF, P}

trait Tinkoff {
  val api: OpenApi
  def toScala[T](cs: CompletionStage[T]): Future[T] = {
    cs match {
      case cf: CF[T] => cf.wrapped
      case _ =>
        val p = new P[T](cs)
        cs whenComplete p
        p.future
    }
  }
}