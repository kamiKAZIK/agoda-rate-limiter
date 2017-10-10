package com.agoda.eduardas.hotel.rest.api

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import argonaut.Argonaut._
import com.agoda.eduardas.hotel.rest.api.SearchHotelResponse._
import de.heikoseeberger.akkahttpargonaut.ArgonautSupport._
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global

final class SearchHotelResponseMarshallingSpec extends FlatSpec with Matchers {
  private implicit val system = ActorSystem()
  private implicit val materializer = ActorMaterializer()

  "SearchHotelResponse" should "be correctly serialized and deserialized" in {
    val response = SearchHotelResponse(List(Hotel(1, "Vilnius", "Deluxe", 150)))
    Marshal(response)
      .to[RequestEntity]
      .flatMap(Unmarshal(_).to[SearchHotelResponse])
      .map(_ shouldBe response)
  }

  "SearchHotelResponse" should "be correctly deserialized from string" in {
    val response = SearchHotelResponse(List(Hotel(1, "Vilnius", "Deluxe", 150)))
    val entity = HttpEntity(`application/json`, """{ "hotels": [{"id": 1, "city": "Vilnius", "room": "Deluxe", "price": 150}] }""")
    Unmarshal(entity).to[SearchHotelResponse].map(_ shouldBe response)
  }
}
