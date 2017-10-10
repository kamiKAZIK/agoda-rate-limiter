package com.agoda.eduardas.hotel.rest.handling.util

import java.time.Instant

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

final class RateLimitExecutorSpec extends FlatSpec with Matchers with MockFactory {
  class RateLimitExecutorStub(private val queue: mutable.Queue[Long], private val tokenLimits: Map[String, Int], private val defaultLimit: Int, private val window: Int) extends RateLimitExecutor(tokenLimits, defaultLimit, window) {
    override def currentTimeInSeconds: Long = queue.dequeue()
  }

  private val tokenLimits = Map("A" -> 5)

  "RateLimitExecutor" should "should pass 5 calls" in {
    val queue = mutable.Queue.empty[Long]

    val currentTime = Instant.now.getEpochSecond
    (0 until 5).foreach { _ =>
      queue.enqueue(currentTime)
    }

    val rateLimitExecutor = new RateLimitExecutorStub(queue, tokenLimits, 5, 10)

    (0 until 5).foreach { _ =>
      rateLimitExecutor.processToken("A")(() => true)(() => false) shouldBe true
    }
  }

  "RateLimitExecutor" should "should reject on limit" in {
    val queue = mutable.Queue.empty[Long]

    val currentTime = Instant.now.getEpochSecond
    (0 until 7).foreach { _ =>
      queue.enqueue(currentTime)
    }
    val rateLimitExecutor = new RateLimitExecutorStub(queue, tokenLimits, 5, 10)

    val acceptance = mockFunction[Boolean]
    acceptance.expects().repeated(5)
    val rejection = mockFunction[Boolean]
    rejection.expects().repeated(2)

    (0 until 7).foreach { _ =>
      rateLimitExecutor.processToken("A")(acceptance)(rejection)
    }
  }

  "RateLimitExecutor" should "should free token from jail" in {
    val queue = mutable.Queue.empty[Long]

    val currentTime = Instant.now.getEpochSecond
    (0 until 6).foreach { _ =>
      queue.enqueue(currentTime)
    }
    queue.enqueue(currentTime + 400)

    val rateLimitExecutor = new RateLimitExecutorStub(queue, tokenLimits, 5, 10)

    val acceptance = mockFunction[Boolean]
    val rejection = mockFunction[Boolean]


    inSequence {
      acceptance.expects().repeated(5)
      rejection.expects().once()
      acceptance.expects().once()
    }

    (0 until 7).foreach { _ =>
      rateLimitExecutor.processToken("A")(acceptance)(rejection)
    }
  }

  "RateLimitExecutor" should "should pass outside window" in {
    val window = 10
    val queue = mutable.Queue.empty[Long]

    val currentTime = Instant.now.getEpochSecond
    (0 until 5).foreach { _ =>
      queue.enqueue(currentTime)
    }
    queue.enqueue(currentTime + window + 1)

    val rateLimitExecutor = new RateLimitExecutorStub(queue, tokenLimits, 5, window)

    val acceptance = mockFunction[Boolean]
    acceptance.expects().repeated(6)
    val rejection = mockFunction[Boolean]
    rejection.expects().never()

    (0 until 6).foreach { _ =>
      rateLimitExecutor.processToken("A")(acceptance)(rejection)
    }
  }

  "RateLimitExecutor" should "should apply global limit" in {
    val globalLimit = 5
    val queue = mutable.Queue.empty[Long]

    val currentTime = Instant.now.getEpochSecond
    (0 until globalLimit + 1).foreach { _ =>
      queue.enqueue(currentTime)
    }

    val rateLimitExecutor = new RateLimitExecutorStub(queue, tokenLimits, globalLimit, 10)

    val acceptance = mockFunction[Boolean]
    acceptance.expects().repeated(5)
    val rejection = mockFunction[Boolean]
    rejection.expects().once()

    (0 until globalLimit + 1).foreach { _ =>
      rateLimitExecutor.processToken("A")(acceptance)(rejection)
    }
  }
}
