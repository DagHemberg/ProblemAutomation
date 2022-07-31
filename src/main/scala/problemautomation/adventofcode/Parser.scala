package problemautomation.adventofcode

import sbt._
import complete.DefaultParsers._
import java.time.LocalDate
import actions._

object Parser {
  type Parser[T] = internal.util.complete.Parser[T] 

  private def flatten(tuple: (Int, (Int, Option[Int]))) = (tuple._1, tuple._2._1, tuple._2._2)

  private def currentlySetYear: Option[Int] = None
  private def currentDate = LocalDate.now()

  private def allYears = {
    val today = currentDate
    val decFirst = LocalDate.of(today.getYear, 12, 1)
    (2015 to today.getYear - (if (today isBefore decFirst) 1 else 0))
      .map(_.toString)
      .toSet
  }

  private def allDays(year: Option[Int] = None) = {
    val all = 1 to 25
    def prepare(n: Int) = if (n.toString.size < 2) s"0$n" else n.toString
    year match {
      case None => all map prepare
      case Some(yr) => {
        val today = currentDate
        (1 to 25)
          .map(day => LocalDate.of(yr, 12, day))
          .filter(date => (date isBefore today) || (date isEqual today))
          .map(date => prepare(date.getDayOfMonth))
      }
    }
  }

  lazy val choose: Parser[actions.Action] = (test | auth | year | stats | fetch | init | submit)

  lazy val auth = token("auth") ~> Space ~> (authSet | authGet | authRetry | authReset)
  lazy val year = token("year") ~> Space ~> (yearSet | yearGet | yearReset)
  lazy val stats = token("stats") ~> Space ~> (statsGet | statsToggle)
  lazy val fetch = token("fetch") ~> Space ~> (parseFetch /* | today */)
  lazy val init = token("init") ~> Space ~> (parseInit /* | today */)
  lazy val submit = token("submit") ~> Space ~> parsePartDayYear map Submit.tupled
  lazy val test = token("test") ^^^ Test

  lazy val parseFetch = parseDayYear map Fetch.tupled
  lazy val parseInit = parseDayYear map Init.tupled

  lazy val parsePartDayYear = (token(parsePart) ~ (Space ~> parseDayYear)) map flatten
  lazy val parseDayYear = (token(parseDay) ~ (Space ~> parseYear).?)

  lazy val parsePart = (token("1") | token("2")) map (_.toInt)

  // lazy val today = token("today") ^^^ Fetch(currentDate.getDayOfMonth, Some(currentDate.getYear))

  lazy val parseDay = {
    val days = allDays().toSet
    NatBasic
      .examples(days)
      .filter(
        days.map(_.toInt).contains, 
        day => s"Invalid day '$day'; choose a day between 1 and ${days.max}."
      )
  }

  lazy val parseYear = {
    NatBasic
      .examples(allYears)
      .filter(
        allYears.map(_.toInt).contains, 
        year => s"Invalid year '$year'; choose a year between ${allYears.min} and ${allYears.max}."
      )
  }

  lazy val authSet = token("set") ~> Space ~> StringBasic.examples("") map Auth.Set
  lazy val authGet = token("get") ^^^ Auth.Get
  lazy val authRetry = token("retry") ^^^ Auth.Retry
  lazy val authReset = token("reset") ^^^ Auth.Reset

  lazy val yearSet = token("set") ~> Space ~> parseYear map Year.Set
  lazy val yearGet = token("get") ^^^ Year.Get
  lazy val yearReset = token("reset") ^^^ Year.Reset

  lazy val statsGet = token("get") ^^^ Stats.Get
  lazy val statsToggle = token("toggle") ~> Space ~> parsePartDayYear map Stats.Toggle.tupled
}