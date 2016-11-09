name := "slick-repository"

val commonSettings = Seq(
  scalaVersion := "2.11.8"
)

val publishSettings = Seq(
  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0")),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  bintrayRepository := "slick-repository",
  bintrayPackageLabels := Seq("slick", "repository")
)

val slick = "com.typesafe.slick" %% "slick" % "3.1.1"

val postgresql = "org.postgresql" % "postgresql" % "9.4.1211"

val slickHikari = "com.typesafe.slick" % "slick-hikaricp_2.11" % "3.1.1" % "test"

val logging = Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
).map(_ % "test")

val scalaTest = "org.scalatest" % "scalatest_2.11" % "3.0.0" % "test"


lazy val `slick-repository-core` = project.
  settings(commonSettings).
  settings(publishSettings).
  settings(Seq(
    libraryDependencies ++= Seq(slick))
  )

lazy val `slick-repository-postgres` = project.
  settings(commonSettings).
  settings(publishSettings).
  settings(libraryDependencies ++= Seq(postgresql) ++ Seq(slickHikari, scalaTest) ++ logging).
  dependsOn(`slick-repository-core`)

