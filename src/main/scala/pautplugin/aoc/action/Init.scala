package pautplugin.aoc.action

import java.time.LocalDate
import pautplugin.aoc.*
import pautplugin.utils.Logging

case class Init(name: String, date: LocalDate) extends Action with Date with AdventAuth {
  val doc = 
    s"""|Downloads and creates the files necessary to solve a specific problem from Advent of Code.
        |
        |# USAGE
        |'aoc init "<name>" <day> [year]'
        |
        |# NOTES
        |- This equivalent to is equivalent to running the following commands:
        |   'aoc data createFiles "<name>" <day> [year]'
        |   'aoc data fetchInput <day> [year]'
        |   'aoc data addExample <day> [year]'
        |${Doc.authNote}
        |${Doc.todayNote}
        |${Doc.dayYearNote}
        |""".stripMargin
  def execute = Logging.fromEither(tokenValue) { _ =>
    Data.CreateFiles(name, date).execute
    Data.FetchInput(date).execute
    Data.AddExample(date).execute
  }
}