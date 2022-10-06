name := "sbt-pAut"

ThisBuild / libraryDependencies += "com.lihaoyi" %% "os-lib" % "0.8.1"

lazy val program = project
  .in(file("program"))
  .settings(
    name := "pAut-program",
    description := "A small library to be used in conjunction with sbt-pAut-plugin that enables easy reading and testing of Advent of Code solutions", 
    version := "0.1.0-SNAPSHOT",
    console / initialCommands := "import paut.aoc._",
    scalaVersion := "2.12.16",
    crossScalaVersions := List("2.12.16", "2.13.7", "3.2.0"),
  )

lazy val plugin = project
  .in(file("plugin"))
  .dependsOn(program)
  .settings(
    name := "sbt-pAut-plugin",
    description := "An sbt plugin that helps automate the process of solving problems from Advent of Code.", 
    version := "0.1.0-SNAPSHOT",
    sbtPlugin := true,
    console / initialCommands := "import pautplugin._, aoc._, utils._, action._",
    libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.8.0",
  )
