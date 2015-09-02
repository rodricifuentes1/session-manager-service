import sbt._

object Dependencies {

  /**
  * Defines repository resolvers
  */
  val resolvers = Seq(
    "Scalaz releases" at "http://dl.bintray.com/scalaz/releases",
    "Sonatype releases" at "http://oss.sonatype.org/content/repositories/releases",
    "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
    "Typesafe releases" at "http://repo.typesafe.com/typesafe/releases/",
    Resolver.bintrayRepo("pellucid", "maven"),
    Resolver.bintrayRepo("rodricifuentes1", "RC-releases")
  )

  // -----------------------------------
  // VERSIONS
  // -----------------------------------

  // Rest api
  val sprayVersion: String = "1.3.3"

  // JSON serializers
  val json4sVersion: String = "3.2.11"

  // Session manager
  val sessionManagerVersion: String = "1.0"

  // Functional programming
  val scalazVersion: String = "7.1.3"
  
  // Logging
  val logbackVersion: String = "1.1.3"
  val scalaloggingVersion: String = "3.1.0"

  // Utils
  val ficusVersion: String = "1.1.2"
  val nScalaTimeVersion: String = "2.2.0"

  // Testing
  val scalatestVersion: String = "2.2.5"

  // -----------------------------------
  // DEPENDENCIES
  // -----------------------------------
  val all = Seq(
    "io.spray" %% "spray-can" % sprayVersion,
    "io.spray" %% "spray-routing" % sprayVersion,
    "io.spray" %% "spray-testkit" % sprayVersion % "test",

    "org.json4s" %% "json4s-native" % json4sVersion,

    "co.rc" %% "session-manager" % sessionManagerVersion,

    "org.scalaz" %% "scalaz-core" % scalazVersion,
    
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaloggingVersion,

    "net.ceedubs" %% "ficus" % ficusVersion,
    "com.github.nscala-time" %% "nscala-time" % nScalaTimeVersion,

    "org.scalatest" % "scalatest_2.11" % scalatestVersion % "test"
  )

}
