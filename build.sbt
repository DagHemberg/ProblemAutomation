name := "sbt-pAut"
ThisBuild / version := "0.1.6"

lazy val plugin = project
  .in(file("."))
  .settings(
    description := "An sbt plugin that helps automate the process of solving problems from Advent of Code.", 
    sbtPlugin := true,
    console / initialCommands := "import pautplugin._, aoc._, utils._, action._",

    libraryDependencies ++= List(
      "io.github.daghemberg" %% "paut-program" % "0.1.4",
      "com.softwaremill.sttp.client3" %% "core" % "3.8.0",
      "com.lihaoyi" %% "os-lib" % "0.8.1",
    ),
  )
