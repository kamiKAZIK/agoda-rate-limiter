package com.agoda.eduardas.hotel.rest.handling.util

import java.time.Instant

import scala.collection.concurrent.TrieMap

class RateLimitExecutor(private val tokenLimits: Map[String, Int], private val defaultLimit: Int, private val window: Int) {
  private val usage = TrieMap.empty[String, Int]
  private val access = TrieMap.empty[String, Long]
  private val jail = TrieMap.empty[String, Long]

  def processToken[R](token: String)(passes: () => R)(rejects: () => R): R = {
    val currentCheckpoint = currentTimeInSeconds

    jail.filter(currentCheckpoint - _._2 > 300).foreach(item => jail.remove(item._1))
    access.filter(entry => currentCheckpoint - entry._2 > 10).foreach(item => {
      usage.remove(item._1)
      access.remove(item._1)
    })

    if (jail.contains(token) && currentCheckpoint - jail(token) < 300) {
      rejects()
    } else {
      access.putIfAbsent(token, currentCheckpoint)
      usage.putIfAbsent(token, 0)
      usage(token) += 1
      if (usage(token) > tokenLimits.getOrElse(token, 5)) {
        jail(token) = currentCheckpoint
        rejects()
      } else {
        passes()
      }
    }
  }

  def currentTimeInSeconds: Long = Instant.now.getEpochSecond
}
