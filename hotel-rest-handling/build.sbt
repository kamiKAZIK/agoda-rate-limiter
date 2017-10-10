name := "hotel-rest-handling"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "de.heikoseeberger" %% "akka-http-argonaut" % "1.17.0",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.10"
)