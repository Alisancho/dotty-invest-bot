package ru.invest.core.converters
import java.util.concurrent.CompletionStage
import monix.eval.Task
import scala.concurrent.Future
import cats.kernel.Monoid
import scala.concurrent.java8.FuturesConvertersImpl.{CF, P}

object MonixTask{
  given as Monoid[Int] {
    def combine(i:Int,l:Int) = i + l
    def unit = 0
  }
  def (i:String) ==>> (o:Int) = i + o.toString
  def (i:String) start  = i + i + i
  
  val wd = "awdaw".start
}