name := "sbt-ProblemAutomation"
sbtPlugin := true
version := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    homepage := Some(url("https://github.com/daghemberg/sbt-problemautomation")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),

    developers := List(
      Developer(
        "DagHemberg",
        "Dag Hemberg",
        "dag.hemberg@gmail.com",
        url("https://scala-lang.org")
      )
    )
  )
