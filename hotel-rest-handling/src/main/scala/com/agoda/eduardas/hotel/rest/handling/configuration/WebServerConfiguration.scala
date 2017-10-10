package com.agoda.eduardas.hotel.rest.handling.configuration

import com.typesafe.config.Config

trait WebServerConfiguration {
  protected def config: Config

  def host: String = config.getString("akka.http.server.host")
  def port: Int = config.getInt("akka.http.server.port")
}
