package ru.invest.core.extension
case class Dddd(j:String)

given Conversion[String, Dddd] = new Dddd(_)
