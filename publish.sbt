import scala.util.Random
import xerial.sbt.Sonatype._

def str = Random.alphanumeric.take(5).mkString

lazy val major = 0
lazy val minor = 1
lazy val patch = 8

lazy val mainVersion = s"$major.$minor.$patch"
def snapshotVersion = s"$major.$minor.${patch + 1}-$str-SNAPSHOT"

ThisBuild / version := mainVersion

versionScheme := Some("early-semver")
organization := "io.github.daghemberg"
sonatypeProfileName := "io.github.daghemberg"
sonatypeCredentialHost := "s01.oss.sonatype.org"
sonatypeProjectHosting := Some(GitHubHosting("DagHemberg", "sbt-pAut", "dag.hemberg@gmail.com"))
publishTo := sonatypePublishToBundle.value

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
