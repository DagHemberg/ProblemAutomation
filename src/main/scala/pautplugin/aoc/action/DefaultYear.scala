package pautplugin.aoc.action

import pautplugin.aoc._
import pautplugin.utils.Logging

object DefaultYear {
  case object Get extends Action {
    val doc = 
      """|Prints the default year.
         |
         |# USAGE
         |'aoc defaultYear get'
         |""".stripMargin

    def execute = Logging.info(s"Default year: ${
      if (os.exists(Files.defaultYearFile)) s"${Date.defaultYear} (manual)"
      else s"${Date.availableYears.max} (dynamic)"
    }")
  }

  case class SetYear(year: Int) extends Action {
    val doc = 
      s"""|Sets the default year.
          |
          |# USAGE
          |'aoc defaultYear set <year>'
          |
          |# NOTES
          |- <year> must be between 2015 and ${Date.availableYears.max}.
          |""".stripMargin

    def execute = {
      os.write.over(Files.defaultYearFile, year.toString, createFolders = true)
      Logging.info(s"Default year set to $year")
    }
  }

  case object Reset extends Action {
    val doc = 
      """|Resets the default year to the latest available year.
         |
         |# USAGE
         |'aoc defaultYear reset'
         |
         |# NOTES
         |- 
         |""".stripMargin

    def execute = {
      os.remove(Files.defaultYearFile)
      Logging.info(s"Default year reset, will now update dynamically (currently ${Date.availableYears.max})")
    }
  }
}
