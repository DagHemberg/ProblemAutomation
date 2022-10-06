import xerial.sbt.Sonatype._

publishMavenStyle := true

ThisBuild / versionScheme := Some("early-semver")
ThisBuild / organization := "io.github.daghemberg"
ThisBuild / sonatypeProfileName := "io.github.daghemberg"
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / licenses := List(librarymanagement.License.MIT)
ThisBuild / sonatypeProjectHosting := Some(GitHubHosting("DagHemberg", "sbt-pAut", "dag.hemberg@gmail.com"))
ThisBuild / publishTo := sonatypePublishToBundle.value
ThisBuild / pomIncludeRepository := { _ => false }

ThisBuild / developers := List(
  librarymanagement.Developer(
    "daghemberg", 
    "Dag Hemberg", 
    "dag.hemberg@gmail.com", 
    url("https://github.com/daghemberg/")
  )
)