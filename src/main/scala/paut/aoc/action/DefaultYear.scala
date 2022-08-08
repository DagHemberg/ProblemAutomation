package paut.aoc.action

import paut.aoc._
import paut.utils.Logging

object Year {
  def default = Files.read(Files.defaultYearFile).map(_.toInt)
  
  case object GetDefault extends Action {
    def execute = Logging.info(s"Default year: ${
      default.getOrElse(s"${Date.availableYears.max} (dynamic)")
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