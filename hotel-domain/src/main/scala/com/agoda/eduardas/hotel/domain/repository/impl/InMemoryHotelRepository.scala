package com.agoda.eduardas.hotel.domain.repository.impl

import com.agoda.eduardas.hotel.domain.database.HotelDatabase
import com.agoda.eduardas.hotel.domain.model.Hotel
import com.agoda.eduardas.hotel.domain.repository.HotelRepository

final class InMemoryHotelRepository(private val database: HotelDatabase) extends HotelRepository {
  override def findAll: Stream[Hotel] = database.rows
}
