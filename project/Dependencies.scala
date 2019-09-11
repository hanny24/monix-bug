import sbt._

object Dependencies {
  lazy val monix = "io.monix" %% "monix" % "3.0.0-RC5"
  lazy val http4sCore = "org.http4s" %% "http4s-core" % "0.20.10"
  lazy val http4sBlaze = "org.http4s" %% "http4s-blaze-server" % "0.20.10"
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % "1.4.0"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8"
}
