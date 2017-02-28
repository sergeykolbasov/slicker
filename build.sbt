lazy val currentScalaVersion = "2.12.1"
lazy val currentSlickVersion = "3.2.0"

val commonSettings = Seq(
  organization := "com.github.imliar",
  scalaVersion := currentScalaVersion,
  crossScalaVersions := Seq("2.11.8", "2.12.0", "2.12.1")
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  publishArtifact in Test := false,
  licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  homepage := Some(url("https://github.com/ImLiar/slicker")),
  autoAPIMappings := true,
  apiURL := Some(url("https://github.com/ImLiar/slicker/tree/master/docs/index.md")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/ImLiar/slicker"),
      "scm:git:git@github.com:ImLiar/slicker.git"
    )
  ),
  pomExtra :=
    <developers>
      <developer>
        <id>ImLiar</id>
        <name>Sergey Kolbasov</name>
        <url>https://liar.ws</url>
      </developer>
    </developers>
)

lazy val noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

val slick = "com.typesafe.slick" %% "slick" % currentSlickVersion

val shapeless = "com.chuusai" %% "shapeless" % "2.3.2"

val postgresql = "org.postgresql" % "postgresql" % "42.0.0"

val slickHikari = "com.typesafe.slick" %% "slick-hikaricp" % currentSlickVersion % "test"

val scalaReflect = "org.scala-lang" % "scala-reflect" % currentScalaVersion

val slickPg = "com.github.tminglei" %% "slick-pg" % "0.15.0-M4"

val logging = Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
).map(_ % "test")

val scalaTest = "org.scalatest" %% "scalatest" % "3.0.0" % "test"


lazy val `slicker-core` = project.
  settings(commonSettings).
  settings(publishSettings).
  settings(Seq(
    libraryDependencies ++= Seq(slick, shapeless, scalaReflect))
  )

lazy val `slicker-postgres` = project.
  settings(commonSettings).
  settings(publishSettings).
  settings(libraryDependencies ++= Seq(postgresql, slickPg) ++ Seq(slickHikari, scalaTest) ++ logging).
  dependsOn(`slicker-core`)

lazy val `slicker-monad` = project.
  settings(commonSettings).
  settings(publishSettings).
  settings(libraryDependencies ++= Seq(slick, scalaTest) ++ logging)

lazy val slicker = project.in(file("."))
  .settings(moduleName := "slicker")
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(noPublish)
  .aggregate(
    `slicker-core`, `slicker-postgres`, `slicker-monad`
  )
  .dependsOn(`slicker-core`, `slicker-postgres`, `slicker-monad`)