package pautplugin.aoc.action

import paut.aoc.Result
import pautplugin.aoc._
import pautplugin.utils.Logging

// import cats.syntax.all._

import java.time.LocalDate

import util.chaining._
import scala.util.Try
import scala.util.Failure
import scala.util.Success

object Results {
  val alreadySubmittedMsg = "This solution has already been submitted and been verified to be correct."
  val parsingErrorMsg = "Error in parsing results, please check the results file with `aoc files openDataFolder`"
  val noResultsMsg = "No results found."

  private def allResults = Files
    .read(Files.resultsFile)
    .toRight(left = noResultsMsg)
    .map(_.split("\n").toList)
    .filterOrElse(_.head != "", noResultsMsg)
    .flatMap { rawResults => 
      Try(rawResults.map(Result.parse)) match {
        case Success(results) => Right(results)
        case Failure(_)       => Left(parsingErrorMsg)
      }
    }

  case object GetAll extends Action {
    def execute = Logging.fromEither(allResults) { results =>
      Logging.info(s"Found ${results.length} results.\n")
      results.mkString("\n------------------\n").foreach(println) 
    }
  }

  case class GetOne(part: Int, date: LocalDate) extends Action with Date {
    def execute = Logging.fromEither(getResult)(println)
    
    def getResult: Either[String, Result] = allResults.flatMap { _
      .find(res => res.day == day && res.year == year && res.part == part)
      .toRight(left = s"No result for part $part on year $year, day $day")
    }
  }
}
