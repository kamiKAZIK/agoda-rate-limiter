package com.agoda.eduardas.hotel.domain.repository

trait Repository[T] {
  def findAll: Stream[T]
}
