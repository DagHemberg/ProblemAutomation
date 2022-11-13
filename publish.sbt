import xerial.sbt.Sonatype._

ThisBuild / version := "0.1.7"

versionScheme := Some("early-semver")
organization := "io.github.daghemberg"
sonatypeProfileName := "io.github.daghemberg"
sonatypeCredentialHost := "s01.oss.sonatype.org"
sonatypeProjectHosting := Some(GitHubHosting("DagHemberg", "sbt-pAut", "dag.hemberg@gmail.com"))
publishTo := sonatypePublishToBundle.value
publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)

licenses := List(librarymanagement.License.MIT)
publishMavenStyle := true
pomIncludeRepository.withRank(KeyRanks.Invisible) := { _ => false }

developers := List(
  librarymanagement.Developer(
    "daghemberg", 
    "Dag Hemberg", 
    "dag.hemberg@gmail.com", 
    url("https://github.com/daghemberg/")
  )
)
