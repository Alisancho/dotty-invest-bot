package ru.invest.core

import java.util.concurrent.CompletionStage

import scala.concurrent.Future
import scala.concurrent.java8.FuturesConvertersImpl.{CF, P}
import monix.eval.Task

object TelegtamHelper {

  type TeComand = "Сбор аналитики" | "Остановка сбора аналитики"
  
  def ko(j:TeComand) = { j match {
//    case "Сбор аналитики" => "wd" ==>> 23
    case "Остановка сбора аналитики" => ""
  }
}

