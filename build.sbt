name := "rate-limiter"

version := "0.1"

scalaVersion := "2.11.11"

lazy val root = Project("rate-limiter", file("."))
  .aggregate(hotelDomain, hotelRestHandling)

lazy val hotelDomain = Project("hotel-domain", file("hotel-domain"))

lazy val hotelRestApi = Project("hotel-rest-api", file("hotel-rest-api"))

lazy val hotelRestHandling = Project("hotel-rest-handling", file("hotel-rest-handling"))
  .dependsOn(hotelDomain, hotelRestApi)