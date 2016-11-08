//
// http://spark.apache.org/docs/latest/quick-start.html#a-standalone-app-in-scala
//
name         := "series-monitor"
organization := "bongani"
version      := "1.0"
scalaVersion := Version.scala

libraryDependencies ++= Dependencies.sparkAkkaHadoop
libraryDependencies += "net.jodah" % "expiringmap" % "0.5.7"


releaseSettings

scalariformSettings

initialCommands in console := """
  |import org.apache.spark._
  |import org.apache.spark.streaming._
  |import org.apache.spark.streaming.StreamingContext._
  |import org.apache.spark.streaming.dstream._
  |import akka.actor.{ActorSystem, Props}
  |import com.typesafe.config.ConfigFactory
  |""".stripMargin


fork in run := true