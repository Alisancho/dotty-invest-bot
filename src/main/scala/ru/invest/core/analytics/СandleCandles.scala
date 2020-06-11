package ru.invest.core.analytics

import ru.tinkoff.invest.openapi.models.market.Candle

object СandleCandles {

  def (candle: Candle).bodyWidth = (candle.closePrice.doubleValue() - candle.openPrice.doubleValue()).abs

  def (candle: Candle).shadowWidth: Double = (candle.lowestPrice.doubleValue() - candle.highestPrice.doubleValue()).abs

  def (candle: Candle).bodyMiddle: Double = {
    val step = (candle.closePrice.doubleValue() - candle.openPrice.doubleValue()).abs / 2
    val min  = candle.closePrice.doubleValue().min(candle.openPrice.doubleValue())
    min + step
  }

  def (candle: Candle).shadowMiddle: Double = {
    val step = (candle.lowestPrice.doubleValue() - candle.highestPrice.doubleValue()).abs / 2
    val min  = candle.lowestPrice.doubleValue().min(candle.highestPrice.doubleValue())
    min + step
  }

  def (candle: Candle).isAbsorptionUp(q2: Candle): Boolean = q2.highestPrice.doubleValue() < candle.closePrice.doubleValue() && q2.lowestPrice.doubleValue > candle.openPrice.doubleValue

  def (candle: Candle).isAbsorptionDown(q2: Candle): Boolean =
    q2.highestPrice.doubleValue() < candle.openPrice
      .doubleValue() && q2.lowestPrice.doubleValue > candle.closePrice.doubleValue

  def (candle: Candle).isHammer: Boolean = candle.openPrice.doubleValue().min(candle.closePrice.doubleValue()) > candle.shadowMiddle && candle.bodyWidth * 3 < candle.shadowWidth

  def (candle: Candle).isRed: Boolean   = candle.openPrice.doubleValue() > candle.closePrice.doubleValue()
  def (candle: Candle).isGreen: Boolean = candle.openPrice.doubleValue() < candle.closePrice.doubleValue()
  

  def (tuple: (Candle, Candle,Candle)).trendDown: Boolean = tuple._1.bodyMiddle > tuple._2.bodyMiddle && tuple._2.bodyMiddle > tuple._3.bodyMiddle
  def (tuple: (Candle, Candle,Candle)).trendUp: Boolean = tuple._1.bodyMiddle < tuple._2.bodyMiddle && tuple._2.bodyMiddle < tuple._3.bodyMiddle
  def (tuple: (Candle, Candle)).trendDown: Boolean = tuple._1.bodyMiddle > tuple._2.bodyMiddle
  def (tuple: (Candle, Candle)).trendUp: Boolean = tuple._1.bodyMiddle < tuple._2.bodyMiddle
  
}
