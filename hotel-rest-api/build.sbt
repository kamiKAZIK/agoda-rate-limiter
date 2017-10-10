name := "hotel-rest-api"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "de.heikoseeberger" %% "akka-http-argonaut" % "1.17.0",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)