package com.agoda.eduardas.hotel.rest.handling.util

import akka.http.scaladsl.server.Rejection

final case class TokenRateLimitRejection(token: String) extends Rejection