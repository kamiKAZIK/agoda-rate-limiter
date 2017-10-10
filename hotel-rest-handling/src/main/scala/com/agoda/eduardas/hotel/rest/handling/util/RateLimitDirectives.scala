package com.agoda.eduardas.hotel.rest.handling.util

import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.server.Directives._

class RateLimitDirectives(private val executor: RateLimitExecutor) {
  def limitRate: Directive0 = headerValueByName("X-Api-Token").flatMap { token =>
    executor.processToken(token) {
      () => pass
    } {
      () => reject(TokenRateLimitRejection(token))
    }
  }
}
