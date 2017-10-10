package com.agoda.eduardas.hotel.domain.service

import com.agoda.eduardas.hotel.domain.model.{Hotel, SortOrder}
import com.agoda.eduardas.hotel.domain.repository.HotelRepository

trait HotelSearchService {
  def hotelRepository: HotelRepository

  def searchHotels(city: String, ordering: Option[SortOrder.Value] = None): Seq[Hotel]
}
