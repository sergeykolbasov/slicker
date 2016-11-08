name := "slick-repository"

val commonSettings = Seq(
  scalaVersion := "2.11.8"
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
  settings(commonSettings ++ Seq(
    libraryDependencies ++= Seq(slick))
  )

lazy val `slick-repository-postgres` = project.
  settings(commonSettings).
  settings(libraryDependencies ++= Seq(postgresql) ++ Seq(slickHikari, scalaTest) ++ logging).
  dependsOn(`slick-repository-core`)

