package pautplugin.aoc.action

import pautplugin.aoc._
import pautplugin.utils.Logging

object Year {
  case object GetDefault extends Action {
    def execute = Logging.info(s"Default year: ${
      if (os.exists(Files.defaultYearFile)) s"${Date.defaultYear} (manual)"
      else s"${Date.availableYears.max} (dynamic)"
    }")
  }

  case class SetDefault(year: Int) extends Action {
    def execute = {
      os.write.over(Files.defaultYearFile, year.toString, createFolders = true)
      Logging.info(s"Default year set to $year")
    }
  }

  case object ResetDefault extends Action {
    def execute = {
      os.remove(Files.defaultYearFile)
      Logging.info(s"Default year reset, will now update dynamically (currently ${Date.availableYears.max})")
    }
  }
}