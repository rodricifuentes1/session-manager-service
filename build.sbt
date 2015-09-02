organization := "co.rc"

name := "session-manager-service"

scalaVersion := "2.11.7"

description := "co.rc session manager rest api layer on top of spray.io"

resolvers ++= Dependencies.resolvers

libraryDependencies ++= Dependencies.all

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Xfuture"
)