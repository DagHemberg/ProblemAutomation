name := "sbt-pAut"
sbtPlugin := true
version := "0.1.0-SNAPSHOT"

console / initialCommands := "import paut._, aoc._, action._"
lazy val githubURL = url("https://github.com/daghemberg/sbt-pAut")

lazy val root = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "os-lib" % "0.8.1",
      "org.jsoup" % "jsoup" % "1.15.2",
    ),
    homepage := Some(githubURL),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "DagHemberg",
        "Dag Hemberg",
        "dag.hemberg@gmail.com",
        githubURL
      )
    )
  )
