package pautplugin.aoc.plugin

import pautplugin.aoc._
import sbt.complete.DefaultParsers._
import sbt.internal.util.complete.Parser
import sbt.{Action => _, Doc => _, Help => _, _}

import java.time.LocalDate

import action._

object Parse {
  lazy val choose = Space ~> (help | auth | defaultYear | results | data)

  lazy val help = token("help") ~> (Space ~> StringBasic
    .examples(Doc.allDocs.keySet - "help"))
    .??("help")
    .map(x => Help(Doc.allDocs.getOrElse(x, EmptyAction)))

  // data
  lazy val data = token("data") ~> Space ~> 
    (initProblem | fetchInput | openExample | addExample | openFolder | openInput)
  lazy val initProblem = token("initProblem") ~> Space ~> nameDayYear map Data.InitProblem.tupled
  lazy val openInput = token("openInput") ~> Space ~> dayYear map Data.OpenInput
  lazy val fetchInput = token("fetchInput") ~> Space ~> dayYear map Data.FetchInput
  lazy val openExample = token("openExample") ~> Space ~> numDayYear map Data.OpenExample.tupled
  lazy val addExample = token("addExample") ~> Space ~> dayYear map Data.AddExample
  lazy val openFolder = token("openFolder") ^^^ Data.OpenFolder

  // auth
  lazy val auth = token("auth") ~> Space ~> (authSet | authGet | authRetry | authReset)
  lazy val authSet = token("set") ~> Space ~> StringBasic.examples("") map Auth.SetSession
  lazy val authGet = token("get") ^^^ Auth.GetSession
  lazy val authRetry = token("retry") ^^^ Auth.Reattempt
  lazy val authReset = token("reset") ^^^ Auth.Reset

  // defaultYear
  lazy val defaultYear = token("defaultYear") ~> Space ~> (yearSet | yearGet | yearReset)
  lazy val yearSet = token("set") ~> Space ~> parseYear map DefaultYear.SetYear
  lazy val yearGet = token("get") ^^^ DefaultYear.Get
  lazy val yearReset = token("reset") ^^^ DefaultYear.Reset

  // results
  lazy val results = token("results") ~> Space ~> (resultsGetAll | resultsGetOne | submit)
  lazy val submit = token("submit") ~> Space ~> partDayYear map Results.Submit.tupled
  lazy val resultsGetAll = token("viewAll") ^^^ Results.GetAll
  lazy val resultsGetOne = token("get") ~> Space ~> partDayYear map Results.GetOne.tupled

  // helpers
  lazy val nameDayYear = parseName ~ (Space ~> dayYear)
  lazy val partDayYear = parsePart ~ (Space ~> dayYear)
  lazy val numDayYear = NatBasic ~ (Space ~> dayYear)
  lazy val dayYear = today | explicitDayYear

  lazy val parseName = StringBasic.examples("\"")
  lazy val parsePart = (token("1") | token("2")) map (_.toInt)

  lazy val explicitDayYear = (token(parseDay) ~ (Space ~> parseYear).?) map (Date.from _).tupled
  lazy val today = 
    token("today")
      .map(_ => Date.today)
      .filter(
        date => date.getMonthValue == 12 && date.getDayOfMonth <= 25, 
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

}