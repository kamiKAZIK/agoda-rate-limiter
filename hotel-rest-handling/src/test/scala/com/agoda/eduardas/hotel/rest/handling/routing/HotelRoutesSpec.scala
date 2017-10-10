package com.agoda.eduardas.hotel.rest.handling.routing

import akka.http.scaladsl.model.StatusCodes.PreconditionFailed
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.agoda.eduardas.hotel.domain.model.Hotel
import com.agoda.eduardas.hotel.domain.repository.HotelRepository
import com.agoda.eduardas.hotel.domain.service.DefaultHotelSearchService
import com.agoda.eduardas.hotel.rest.api.SearchHotelResponse
import com.typesafe.config.{ConfigFactory, ConfigValueFactory}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import de.heikoseeberger.akkahttpargonaut.ArgonautSupport._
import com.agoda.eduardas.hotel.rest.api.SearchHotelResponse._

final class HotelRoutesSpec extends FlatSpec with Matchers with ScalatestRouteTest with MockFactory {
  private val config = ConfigFactory.empty()
    .withValue("hotel.rate-limit.window", ConfigValueFactory.fromAnyRef(10))
    .withValue("hotel.rate-limit.default-rate", ConfigValueFactory.fromAnyRef(5))

  "HotelRoutes" should "return unsorted Vilnius hotels" in {
    val hotels = Stream(
      Hotel(1, "Vilnius", "Deluxe", 100),
      Hotel(2, "Vilnius", "Standard", 50)
    )

    val hotelResponse = SearchHotelResponse(hotels.map(hotel => SearchHotelResponse.Hotel(
      hotel.id,
      hotel.city,
      hotel.room,
      hotel.price
    )).toList)

    val hotelRepository = mock[HotelRepository]
    (hotelRepository.findByCityOrderByPrice _).expects("Vilnius", None).returning(hotels).once()
    val hotelSearchService = new DefaultHotelSearchService(hotelRepository)
    val hotelRoutes = new HotelRoutes(hotelSearchService, config)
    Get("/hotel?city=Vilnius").addHeader(RawHeader("X-Api-Token", "T")) ~> hotelRoutes.routes ~> check {
      responseAs[SearchHotelResponse] shouldEqual hotelResponse
    }
  }

  "HotelRoutes" should "return ascending sorted Vilnius hotels" in {
    val hotels = Stream(
      Hotel(1, "Vilnius", "Deluxe", 100),
      Hotel(2, "Vilnius", "Standard", 50)
    )

    val hotelResponse = SearchHotelResponse(hotels.sortBy(_.price).map(hotel => SearchHotelResponse.Hotel(
      hotel.id,
      hotel.city,
      hotel.room,
      hotel.price
    )).toList)

    val hotelRepository = mock[HotelRepository]
    (hotelRepository.findByCityOrderByPrice _).expects("Vilnius", Some(Ordering[Int])).returning(hotels.sortBy(_.price)).once()
    val hotelSearchService = new DefaultHotelSearchService(hotelRepository)
    val hotelRoutes = new HotelRoutes(hotelSearchService, config)
    Get("/hotel?city=Vilnius&sorting=asc").addHeader(RawHeader("X-Api-Token", "T")) ~> hotelRoutes.routes ~> check {
      responseAs[SearchHotelResponse] shouldEqual hotelResponse
    }
  }

  "HotelRoutes" should "return descending sorted Vilnius hotels" in {
    val hotels = Stream(
      Hotel(2, "Vilnius", "Standard", 50),
      Hotel(1, "Vilnius", "Deluxe", 100)
    )

    val hotelResponse = SearchHotelResponse(hotels.sortBy(_.price).reverse.map(hotel => SearchHotelResponse.Hotel(
      hotel.id,
      hotel.city,
      hotel.room,
      hotel.price
    )).toList)

    val hotelRepository = mock[HotelRepository]
    (hotelRepository.findByCityOrderByPrice _)
      .expects("Vilnius", argThat[Option[Ordering[Int]]](x => x.get.lt(2, 1)))
      .returning(hotels.sortBy(_.price).reverse)
      .once()

    val hotelSearchService = new DefaultHotelSearchService(hotelRepository)
    val hotelRoutes = new HotelRoutes(hotelSearchService, config)
    Get("/hotel?city=Vilnius&sorting=desc").addHeader(RawHeader("X-Api-Token", "T")) ~> hotelRoutes.routes ~> check {
      responseAs[SearchHotelResponse] shouldEqual hotelResponse
    }
  }

  "HotelRoutes" should "return unsorted Vilnius hotels when sorting is invalid" in {
    val hotels = Stream(
      Hotel(1, "Vilnius", "Deluxe", 100),
      Hotel(2, "Vilnius", "Standard", 50)
    )

    val hotelResponse = SearchHotelResponse(hotels.map(hotel => SearchHotelResponse.Hotel(
      hotel.id,
      hotel.city,
      hotel.room,
      hotel.price
    )).toList)

    val hotelRepository = mock[HotelRepository]
    (hotelRepository.findByCityOrderByPrice _).expects("Vilnius", None).returning(hotels).once()
    val hotelSearchService = new DefaultHotelSearchService(hotelRepository)
    val hotelRoutes = new HotelRoutes(hotelSearchService, config)
    Get("/hotel?city=Vilnius&sorting=dummy").addHeader(RawHeader("X-Api-Token", "T")) ~> hotelRoutes.routes ~> check {
      responseAs[SearchHotelResponse] shouldEqual hotelResponse
    }
  }

  "HotelRoutes" should "return empty List when nothing is found" in {
    val hotelRepository = mock[HotelRepository]
    (hotelRepository.findByCityOrderByPrice _).expects("Birzai", None).returning(Stream()).once()
    val hotelSearchService = new DefaultHotelSearchService(hotelRepository)
    val hotelRoutes = new HotelRoutes(hotelSearchService, config)
    Get("/hotel?city=Birzai").addHeader(RawHeader("X-Api-Token", "T")) ~> hotelRoutes.routes ~> check {
      responseAs[SearchHotelResponse] shouldEqual SearchHotelResponse(List.empty)
    }
  }

  "HotelRoutes" should "should reject token" in {
    val hotelRepository = mock[HotelRepository]
    (hotelRepository.findByCityOrderByPrice _).expects("Birzai", None).returning(Stream()).repeated(5)
    val hotelSearchService = new DefaultHotelSearchService(hotelRepository)
    val callLimit = 5
    val hotelRoutes = new HotelRoutes(
      hotelSearchService, config
        .withValue("hotel.rate-limit.tokens.token-3", ConfigValueFactory.fromAnyRef(callLimit))
    )
    (0 until callLimit).foreach { _ =>
      Get("/hotel?city=Birzai").addHeader(RawHeader("X-Api-Token", "T")) ~> hotelRoutes.routes ~> check {
        responseAs[SearchHotelResponse] shouldEqual SearchHotelResponse(List.empty)
      }
    }
    Get("/hotel?city=Birzai").addHeader(RawHeader("X-Api-Token", "T")) ~> hotelRoutes.routes ~> check {
      status shouldBe PreconditionFailed
    }
  }
}
