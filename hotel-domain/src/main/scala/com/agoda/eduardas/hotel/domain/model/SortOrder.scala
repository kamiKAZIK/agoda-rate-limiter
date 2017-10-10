package com.agoda.eduardas.hotel.domain.model

object SortOrder extends Enumeration {
  type WeekDay = Value
  val ASC, DESC = Value

  def withNameOpt(s: String): Option[Value] = values.find(_.toString.equalsIgnoreCase(s))
}