package com.agoda.eduardas.hotel.domain.service

import com.agoda.eduardas.hotel.domain.model.{Hotel, SortOrder}
import com.agoda.eduardas.hotel.domain.repository.HotelRepository
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

final class DefaultHotelSearchServiceSpec extends FlatSpec with Matchers with MockFactory {
  "Hotel Service" should "query results without sorting" in {
    val expected = Seq(Hotel(1, "Vilnius", "Super", 20)).toStream

    val hotelRepository = mock[HotelRepository]
    (hotelRepository.findByCityOrderByPrice _).expects("Vilnius", None).returning(expected)
    val hotelService = new DefaultHotelSearchService(hotelRepository)
    hotelService.searchHotels("Vilnius") shouldBe expected
  }

  "Hotel Service" should "query results with ascending sorting" in {
    val expected = Seq(Hotel(2, "Kaunas", "Super", 50)).toStream

    val hotelRepository = mock[HotelRepository]
    (hotelRepository.findByCityOrderByPrice _).expects("Kaunas", Some(Ordering[Int])).returning(expected)
    val hotelService = new DefaultHotelSearchService(hotelRepository)
    hotelService.searchHotels("Kaunas", Some(SortOrder.ASC)) shouldBe expected
  }

  "Hotel Service" should "query results with descending sorting" in {
    val expected = Seq(Hotel(3, "Panevezys", "Super", 30)).toStream

    val hotelRepository = mock[HotelRepository]
    (hotelRepository.findByCityOrderByPrice _).expects("Panevezys", argThat[Option[Ordering[Int]]](x => x.get.lt(2, 1))).returning(expected)
    val hotelService = new DefaultHotelSearchService(hotelRepository)
    hotelService.searchHotels("Panevezys", Some(SortOrder.DESC)) shouldBe expected
  }
}
