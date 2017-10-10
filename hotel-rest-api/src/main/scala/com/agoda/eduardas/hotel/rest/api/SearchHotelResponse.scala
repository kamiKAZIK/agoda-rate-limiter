package com.agoda.eduardas.hotel.rest.api

import argonaut.Argonaut.{casecodec1, casecodec4}
import argonaut.CodecJson

final case class SearchHotelResponse(hotels: List[SearchHotelResponse.Hotel])

object SearchHotelResponse {
  final case class Hotel(id: Int, city: String, room: String, price: Int)

  implicit def hotelCodec: CodecJson[SearchHotelResponse.Hotel] =
    casecodec4(Hotel.apply, Hotel.unapply)("id", "city", "room", "price")

  implicit def searchHotelResponseCodec: CodecJson[SearchHotelResponse] =
    casecodec1(SearchHotelResponse.apply, SearchHotelResponse.unapply)("hotels")
}
