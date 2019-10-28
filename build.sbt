name := "lightningcli"

version := "0.1.0"

description := "Lightning Client"

organization := "mathbot.com"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "org.scala-sbt.ipcsocket" % "ipcsocket" % "1.0.0",
  "io.spray" % "spray-json_2.13" % "1.3.5"
)
