package paut.aoc.action

import paut.aoc._
import paut.utils.Logging
import java.time.LocalDate
import util.chaining._

object Results {
  val alreadySubmittedMsg = "This solution has already been submitted and been verified to be correct."

  private def all = Files.read(Files.resultsFile).map(_.split("\n").toList).getOrElse(Nil)

  case object GetAll extends Action {
    def execute = all map Result.parse foreach println
  }

  case class GetOne(part: Int, date: LocalDate) extends Action with Date {
    def execute = 
      Logging.fromOption(get, s"No result for part $part on year $year, day $day")(println)
    
    def get: Option[Result] = {
      all.tap(_.foreach(println)) find { _ startsWith s"$year;$day;$part" } map Result.parse
    }
  }
}
