package com.agoda.eduardas.hotel.domain.repository.impl

import com.agoda.eduardas.hotel.domain.database.HotelDatabase
import com.agoda.eduardas.hotel.domain.model.Hotel
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers, OneInstancePerTest}

final class InMemoryHotelRepositorySpec extends FlatSpec with Matchers with MockFactory with OneInstancePerTest {
  val hotels = Seq(
    Hotel(1, "Vilnius", "Super", 20),
    Hotel(2, "Vilnius", "Super", 10),
    Hotel(3, "Vilnius", "Super", 60),
    Hotel(4, "Kaunas", "Super", 50),
    Hotel(5, "Panevezys", "Super", 30)
  ).toStream
  val hotelDatabaseMock = mock[HotelDatabase]
  (hotelDatabaseMock.rows _).expects().returns(hotels).anyNumberOfTimes()
  val repository = new InMemoryHotelRepository(hotelDatabaseMock)

  "InMemoryHotelRepository" should "return all hotels" in {
    repository.findAll shouldBe hotels
  }

  "InMemoryHotelRepository" should "return Vilnius hotels" in {
    repository.findByCityOrderByPrice("Vilnius") shouldBe Stream(hotels(0), hotels(1), hotels(2))
  }

  "InMemoryHotelRepository" should "return Vilnius hotels with ascending order" in {
    repository.findByCityOrderByPrice("Vilnius", Some(Ordering[Int])) shouldBe Stream(hotels(1), hotels(0), hotels(2))
  }

  "InMemoryHotelRepository" should "return Vilnius hotels with descending order" in {
    repository.findByCityOrderByPrice("Vilnius", Some(Ordering[Int].reverse)) shouldBe Stream(hotels(2), hotels(0), hotels(1))
  }
}