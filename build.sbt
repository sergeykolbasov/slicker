val commonSettings = Seq(
  scalaVersion := "2.11.8"
)

val publishSettings = Seq(
  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0")),
  organization := "slicker",
  publishArtifact := true,
  publishMavenStyle := true,
  publishArtifact in Test := false,
  bintrayRepository := "slicker",
  bintrayPackageLabels := Seq("slick", "repository")
)

val slick = "com.typesafe.slick" %% "slick" % "3.1.1"

val shapeless = "com.chuusai" %% "shapeless" % "2.3.2"

val postgresql = "org.postgresql" % "postgresql" % "9.4.1211"

val slickHikari = "com.typesafe.slick" % "slick-hikaricp_2.11" % "3.1.1" % "test"

val slickPg = Seq(
  "com.github.tminglei" %% "slick-pg" % "0.14.3",
  "com.github.tminglei" %% "slick-pg_date2" % "0.14.3"
)

val logging = Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
).map(_ % "test")

val scalaTest = "org.scalatest" % "scalatest_2.11" % "3.0.0" % "test"


lazy val `slicker-core` = project.
  settings(commonSettings).
  settings(publishSettings).
  settings(Seq(
    libraryDependencies ++= Seq(slick, shapeless))
  )

lazy val `slicker-postgres` = project.
  settings(commonSettings).
  settings(publishSettings).
  settings(libraryDependencies ++= Seq(postgresql) ++ slickPg ++ Seq(slickHikari, scalaTest) ++ logging).
  dependsOn(`slicker-core`)

lazy val `slicker-monad` = project.
  settings(commonSettings).
  settings(publishSettings).
  settings(libraryDependencies ++= Seq(slick, scalaTest) ++ logging)
