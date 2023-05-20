import sbt.Keys.{libraryDependencies, scalaVersion, version}

lazy val root = (project in file(".")).
  settings(
    name := "CSE511",
    version := "0.1.0",
    scalaVersion := "2.12.10",
    organization  := "org.datasyslab",
    publishMavenStyle := true,
    mainClass := Some("cse511.Main")
  )

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.2.0" % "provided",
  "org.apache.spark" %% "spark-sql" % "3.2.0" % "provided",
  "org.scalatest" %% "scalatest" % "3.2.10" % "test",
  "org.specs2" %% "specs2-core" % "4.12.3" % "test",
  "org.specs2" %% "specs2-junit" % "4.12.3" % "test"
)
