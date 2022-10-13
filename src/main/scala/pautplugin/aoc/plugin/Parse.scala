package pautplugin.aoc.plugin

import sbt.complete.DefaultParsers._
import sbt.internal.util.complete.Parser
import sbt.{Action => _, _}

import java.time.LocalDate

import pautplugin.aoc._
import action._

object Parse {
  lazy val choose: Parser[Action] = Space ~> (settings | results | files)

  lazy val settings = token("settings") ~> Space ~> (auth | defaultYear)
  lazy val files = token("files") ~> Space ~> (initProblem | openExample | addExample | openDataFolder)
  lazy val results = token("results") ~> Space ~> (resultsGetAll | resultsGetOne | submit)

  lazy val auth = token("auth") ~> Space ~> (authSet | authGet | authRetry | authReset)
  lazy val defaultYear = token("defaultYear") ~> Space ~> (yearSet | yearGet | yearReset)

  lazy val initProblem = token("initProblem") ~> Space ~> nameDayYear map InitProblemFiles.tupled
  lazy val openExample = token("openExample") ~> Space ~> partDayYear map Data.OpenExample.tupled
  lazy val addExample = token("addExample") ~> Space ~> dayYear map Data.AddExample
  lazy val openDataFolder = token("openDataFolder") ^^^ Data.OpenFolder

  lazy val submit = token("submit") ~> Space ~> partDayYear map SubmitSolution.tupled
  lazy val resultsGetAll = token("view") ^^^ Results.GetAll
  lazy val resultsGetOne = token("get") ~> Space ~> partDayYear map Results.GetOne.tupled

  lazy val nameDayYear = parseName ~ (Space ~> dayYear)
  lazy val partDayYear = parsePart ~ (Space ~> dayYear)
  lazy val dayYear = today | explicitDayYear

  lazy val parseName = StringBasic.examples("\"")
  lazy val parsePart = (token("1") | token("2")) map (_.toInt)

  lazy val explicitDayYear = (token(parseDay) ~ (Space ~> parseYear).?) map (Date.from _).tupled
  lazy val today = 
    token("today")
      .map(_ => Date.today)
      .filter(
        date => date.getMonthValue == 12 && Date.today.getDayOfMonth <= 25, 
        _ => "No problem was published today."
      )
      .map(date => Date.from(date.getDayOfMonth, Some(date.getYear)))

  lazy val parseDay = {
    val days = Date.availableDays.map(day => f"$day%02d").toSet
    NatBasic
      .examples(days + "today")
      .filter(
        days.map(_.toInt).contains, 
        day => s"Invalid day '$day'; choose a day between 1 and ${days.max}."
      )
  }

  lazy val parseYear = {
    val allYears = Date.availableYears.map(_.toString).toSet
    NatBasic
      .examples(allYears)
      .filter(
        allYears.map(_.toInt).contains, 
        year => s"Invalid year '$year'; choose a year between ${allYears.min} and ${allYears.max}."
      )
  }

  lazy val authSet = token("set") ~> Space ~> StringBasic.examples("") map Auth.SetSession
  lazy val authGet = token("get") ^^^ Auth.GetSession
  lazy val authRetry = token("retry") ^^^ Auth.Reattempt
  lazy val authReset = token("reset") ^^^ Auth.Reset

  lazy val yearSet = token("set") ~> Space ~> parseYear map Year.SetDefault
  lazy val yearGet = token("get") ^^^ Year.GetDefault
  lazy val yearReset = token("reset") ^^^ Year.ResetDefault

}