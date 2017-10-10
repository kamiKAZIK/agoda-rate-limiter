package com.agoda.eduardas.hotel.domain.database

import com.agoda.eduardas.hotel.domain.model.Hotel

import scala.io.Source

class HotelDatabase(databasePath: String) {
  lazy val container = parse(databasePath)

  def rows: Stream[Hotel] = container

  private def parse(databasePath: String): Stream[Hotel] = loadSource(databasePath)
    .getLines
    .drop(1)
    .map(line => {
      val parts = line.split(',').map(_.trim)
      Hotel(Integer.parseInt(parts(1)), parts(0), parts(2), Integer.parseInt(parts(3)))
    }).toStream

  def loadSource(databasePath: String): Source =
    Source.fromFile(databasePath)
}
