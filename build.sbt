name := "slick-repository"

lazy val commonSettings = Seq(
  scalaVersion := "2.11.8"
)

lazy val slickVersion = "3.1.1"

lazy val `slick-repository-core` = project.
  settings(commonSettings ++ Seq(
    libraryDependencies ++= Seq(
      "com.typesafe.slick" %% "slick" % slickVersion
    ))
  )

lazy val `slick-repository-postgres` = project.settings(commonSettings).dependsOn(`slick-repository-core`)

