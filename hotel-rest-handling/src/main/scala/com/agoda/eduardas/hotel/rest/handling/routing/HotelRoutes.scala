package com.agoda.eduardas.hotel.rest.handling.routing

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.PreconditionFailed
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RejectionHandler, Route}
import com.agoda.eduardas.hotel.domain.model.SortOrder
import com.agoda.eduardas.hotel.domain.service.HotelSearchService
import com.agoda.eduardas.hotel.rest.api.SearchHotelResponse
import com.agoda.eduardas.hotel.rest.api.SearchHotelResponse._
import com.agoda.eduardas.hotel.rest.handling.configuration.RateLimitConfiguration
import com.agoda.eduardas.hotel.rest.handling.util.{RateLimitDirectives, RateLimitExecutor, TokenRateLimitRejection}
import com.typesafe.config.Config
import de.heikoseeberger.akkahttpargonaut.ArgonautSupport._

class HotelRoutes(private val hotelSearchService: HotelSearchService, override protected val config: Config) extends RateLimitConfiguration {
  private val limitDirectives = new RateLimitDirectives(new RateLimitExecutor(rateTokens, defaultRate, rateWindow))
  import limitDirectives.limitRate

  private def rejectionHandler = RejectionHandler.newBuilder()
    .handle {
      case TokenRateLimitRejection(token) =>
        complete(HttpResponse(PreconditionFailed, entity = s"Token $token has reached its rate limit!"))
    }.result()

  def routes: Route = handleRejections(rejectionHandler) {
    path("hotel") {
      get {
        limitRate {
          parameters('city, 'sorting.?) { (city, sorting) =>
            complete {
              SearchHotelResponse(
                hotelSearchService.searchHotels(city, sorting.flatMap(s => SortOrder.withNameOpt(s)))
                  .map(hotel => SearchHotelResponse.Hotel(
                    hotel.id,
                    hotel.city,
                    hotel.room,
                    hotel.price
                  )).toList
              )
            }
          }
        }
      }
    }
  }
}
