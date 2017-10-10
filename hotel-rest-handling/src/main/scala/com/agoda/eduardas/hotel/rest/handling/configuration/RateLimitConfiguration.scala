package com.agoda.eduardas.hotel.rest.handling.configuration

import com.typesafe.config.Config

import scala.collection.JavaConverters._

trait RateLimitConfiguration {
  protected def config: Config

  def rateWindow: Int = config.getInt("hotel.rate-limit.window")
  def defaultRate: Int = config.getInt("hotel.rate-limit.default-rate")
  def rateTokens: Map[String, Int] = {
    val path = "hotel.rate-limit.tokens"
    if (!config.hasPath(path)) {
      Map.empty
    } else {
      val root = config.getConfig(path)
      root.entrySet()
        .asScala
        .map(entry => entry.getKey -> root.getInt(entry.getKey))
        .toMap
    }
  }
}
