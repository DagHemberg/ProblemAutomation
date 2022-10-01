lazy val githubURL = url("https://github.com/daghemberg/sbt-pAut")

name := "sbt-pAut"

ThisBuild / libraryDependencies += "com.lihaoyi" %% "os-lib" % "0.8.1"
ThisBuild / organization := "org.daghe"
ThisBuild / licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / homepage := Some(githubURL)
ThisBuild / crossScalaVersions := List("2.12.16", "2.13.7", "3.2.0")
ThisBuild / developers := List(
  Developer(
    "DagHemberg",
    "Dag Hemberg",
    "dag.hemberg@gmail.com",
    githubURL
  )
)

lazy val program = project
  .in(file("program"))
  .settings(
    name := "pAut-program",
    console / initialCommands := "import paut.aoc._",
    scalaVersion := "2.12.16",
  )

lazy val plugin = project
  .in(file("plugin"))
  .dependsOn(program)
  .settings(
    name := "sbt-pAut-plugin",
    sbtPlugin := true,
    console / initialCommands := "import pautplugin._, aoc._, utils._, action._",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3" %% "core" % "3.8.0",
    )
  )
