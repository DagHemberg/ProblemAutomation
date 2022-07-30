package problemautomation.adventofcode

import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin

object AdventOfCodePlugin extends AutoPlugin {   
  override def trigger = allRequirements
  override def requires = plugins.JvmPlugin
  
  object autoImport {
    val aocTask = inputKey[Unit]("")
  }
  
  import autoImport._

  override lazy val projectSettings = Seq(
    aocTask := {
      val action = Parser.choose.parsed
      println(action)
      // action.execute
    }
  )

  override lazy val buildSettings = Nil

  override lazy val globalSettings = Nil
}
