package com.agoda.eduardas.hotel.domain.model

import org.scalatest.{FlatSpec, Matchers}

final class SortOrderSpec extends FlatSpec with Matchers {
  "SortOrder Enumeration" should "find values by name" in {
    SortOrder.withNameOpt("ASC") shouldBe Some(SortOrder.ASC)
    SortOrder.withNameOpt("DESC") shouldBe Some(SortOrder.DESC)
    SortOrder.withNameOpt("asc") shouldBe Some(SortOrder.ASC)
    SortOrder.withNameOpt("desc") shouldBe Some(SortOrder.DESC)
    SortOrder.withNameOpt("Asc") shouldBe Some(SortOrder.ASC)
    SortOrder.withNameOpt("Desc") shouldBe Some(SortOrder.DESC)
  }

  "SortOrder Enumeration" should "be none in case value is not found" in {
    SortOrder.withNameOpt("DUMMY") shouldBe None
  }
}
