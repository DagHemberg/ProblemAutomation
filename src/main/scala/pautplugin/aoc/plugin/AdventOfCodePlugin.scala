package pautplugin.aoc.plugin

import sbt._
import sbt.Keys._

object AdventOfCodePlugin extends AutoPlugin {   
  override def trigger = allRequirements
  override def requires = plugins.JvmPlugin
  
  object autoImport {
    val aoc = inputKey[Unit]("Performs a pAut action. See 'aoc help' for more info.")
  }
  
  import autoImport._

  override lazy val projectSettings = Seq(
    aoc := {
      val action = Parse.choose.parsed
      action.execute
    }
  )
}
