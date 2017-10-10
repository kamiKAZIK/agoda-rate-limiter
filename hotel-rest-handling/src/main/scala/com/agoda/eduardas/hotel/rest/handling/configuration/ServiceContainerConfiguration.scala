package com.agoda.eduardas.hotel.rest.handling.configuration

import com.typesafe.config.Config

trait ServiceContainerConfiguration {
  protected def config: Config

  def dataPath: String = config.getString("hotel.data.path")
}
