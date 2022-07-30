package problemautomation.adventofcode

import sbt._
import sbt.Keys._

object AdventOfCodePlugin extends AutoPlugin {   
  override def trigger = allRequirements
  override def requires = plugins.JvmPlugin
  
  object autoImport {
    val aoc = inputKey[Unit]("")
  }
  
  import autoImport._

  override lazy val projectSettings = Seq(
    aoc := {
      val action = Parser.choose.parsed
      println(action)
    }
  )

  override lazy val buildSettings = Nil

  override lazy val globalSettings = Nil
}
