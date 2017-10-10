package com.agoda.eduardas.hotel.rest.handling

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.PreconditionFailed
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.RejectionHandler
import akka.http.scaladsl.settings.ServerSettings
import com.agoda.eduardas.hotel.rest.handling.configuration.WebServerConfiguration
import com.agoda.eduardas.hotel.rest.handling.util.TokenRateLimitRejection
import com.typesafe.config.ConfigFactory

object WebServer extends App with WebServerConfiguration {
  override protected val config = ConfigFactory.load

  implicit def rejectionHandler = RejectionHandler.newBuilder()
    .handle {
      case TokenRateLimitRejection(token) =>
        complete(HttpResponse(PreconditionFailed, entity = s"Token $token has reached its rate limit!"))
    }.result()
  
  new ServiceContainer(config).startServer(host, port, ServerSettings(config))
}
