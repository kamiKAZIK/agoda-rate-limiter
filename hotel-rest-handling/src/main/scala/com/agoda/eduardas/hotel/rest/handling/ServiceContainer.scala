package com.agoda.eduardas.hotel.rest.handling

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.PreconditionFailed
import akka.http.scaladsl.server._
import com.agoda.eduardas.hotel.domain.database.HotelDatabase
import com.agoda.eduardas.hotel.domain.repository.impl.InMemoryHotelRepository
import com.agoda.eduardas.hotel.domain.service.DefaultHotelSearchService
import com.agoda.eduardas.hotel.rest.handling.configuration.ServiceContainerConfiguration
import com.agoda.eduardas.hotel.rest.handling.routing.HotelRoutes
import com.agoda.eduardas.hotel.rest.handling.util.TokenRateLimitRejection
import com.typesafe.config.Config

class ServiceContainer(override protected val config: Config) extends HttpApp with ServiceContainerConfiguration {
  implicit def rejectionHandler = RejectionHandler.newBuilder()
    .handle {
      case TokenRateLimitRejection(token) =>
        complete(HttpResponse(PreconditionFailed, entity = s"Token $token has reached its rate limit!"))
    }.result()

  override def routes: Route = new HotelRoutes(
    new DefaultHotelSearchService(
      new InMemoryHotelRepository(
        new HotelDatabase(dataPath)
      )
    ),
    config
  ).routes
}
