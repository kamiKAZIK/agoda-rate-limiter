package com.agoda.eduardas.hotel.domain.repository

import com.agoda.eduardas.hotel.domain.model.Hotel

trait HotelRepository extends Repository[Hotel] {
  def findByCityOrderByPrice(city: String, ordering: Option[Ordering[Int]] = None): Stream[Hotel] = {
    val foundHotels = findAll.filter(_.city.equalsIgnoreCase(city))
    ordering.fold(foundHotels)(foundHotels.sortBy(_.price)(_))
  }
}
