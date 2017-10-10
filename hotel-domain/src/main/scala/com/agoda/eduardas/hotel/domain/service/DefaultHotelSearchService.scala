package com.agoda.eduardas.hotel.domain.service

import com.agoda.eduardas.hotel.domain.model.{Hotel, SortOrder}
import com.agoda.eduardas.hotel.domain.repository.HotelRepository

class DefaultHotelSearchService(override val hotelRepository: HotelRepository) extends HotelSearchService {
  override def searchHotels(city: String, ordering: Option[SortOrder.Value] = None): Seq[Hotel] = {
    hotelRepository.findByCityOrderByPrice(city, ordering.map(_ match {
      case SortOrder.ASC => Ordering[Int]
      case SortOrder.DESC => Ordering[Int].reverse
    }))
  }
}
