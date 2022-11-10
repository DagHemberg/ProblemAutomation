package pautplugin.aoc.action

import paut.aoc.Result
import pautplugin.aoc._
import pautplugin.utils._

import java.time.LocalDate

import util.chaining._
import scala.util.Try
import scala.util.Failure
import scala.util.Success

case object Results {
  val alreadySubmittedMsg = "This solution has already been submitted and been verified to be correct."
  val parsingErrorMsg = "Error in parsing results, please check the results file with 'aoc data openFolder'"
  val noResultsMsg = "No results found."

  private def allResults = Files
    .read(Files.resultsFile)
    .toRight(left = noResultsMsg)
    .map(_.split("\n").toList)
    .filterOrElse(_.head != "", noResultsMsg)
    .flatMap { rawResults => 
      Try(rawResults.map(Result.parse))
        .toEither
        .left.map(_ => parsingErrorMsg)
    }

  case object GetAll extends Action {
    val doc = 
      """|Prints all results produced so far.
         |
         |# USAGE
         |'aoc results viewAll'
         |""".stripMargin

    def execute = Logging.fromEither(allResults) { results =>
      Logging.info(s"Found ${results.length} result${if (results.length > 1) "s" else ""}.\n")
      println(results.mkString("\n\n"))
    }
  }

  case class GetOne(part: Int, date: LocalDate) extends Action with Date {
    val doc = 
      s"""|Prints the result for the given day and version.
          |
          |# USAGE
          |'aoc results get <part> <today | <day> [year]>'
          |
          |# NOTES
          |${Doc.part}
          |${Doc.today}
          |${Doc.dayYear}
          |""".stripMargin

    def getResult = allResults.flatMap { _
      .find(res => res.day == day && res.year == year && res.part == part)
      .toRight(left = s"No result for part $part on year $year, day $day")
    }

    def execute = Logging.fromEither(getResult)(println)
  }

  case class Submit(part: Int, date: LocalDate) extends Action with Date with AdventAuth {
    val doc = 
      s"""|Submits the solution for the given day and version.
          |
          |# USAGE
          |'aoc results submit <part> <today | <day> [year]>'
          |
          |# NOTES
          |${Doc.auth}
          |${Doc.part}
          |${Doc.dayYear}
          |${Doc.today}
          |""".stripMargin

    private val notCorrectMsg = "That's not the correct answer."

    private def too(msg: String) = s"Your answer is ${Logging.red(s"too $msg")}."
    private val tooHighMsg = too("high")
    private val tooLowMsg = too("low")

    private def wait(text: String) = {
      val r1 = "(?<=  Please wait )(.*)(?= before)".r.findFirstIn(text)
      val r2 = "(?<=  You have )(.*)(?= left to wait)".r.findFirstIn(text)
      val time = r1.orElse(r2).getOrElse("<N/A>")
      Logging.error(s"Please wait $time before submitting another answer.")
    }

    private def post = GetOne(part, date)
      .getResult
      .flatMap { res => 
        if (res.submitted) Left(alreadySubmittedMsg)
        else Request.post(
          data = Map("level" -> part.toString, "answer" -> res.solution),
          url = problemUrl(date.getYear, date.getDayOfMonth, "answer"), 
        )
      }

    def execute = Logging.fromEither(post) { text =>
      if (text.contains("too recently")) {
        Logging.error("You submitted an answer too recently.")
        wait(text)
      }

      else if (text.contains("not the right answer")) {
        Logging.error(notCorrectMsg)
        if (text.contains("too high")) Logging.error(tooHighMsg)
        else if (text.contains("too low")) Logging.error(tooLowMsg)
        wait(text)
      }

      // shouldn't really be needed, but adding just in case
      else if (text.contains("You don't seem to be solving the right level")) {
        Logging.error("You don't seem to be solving the right level. Either you don't have access to this problem yet or you've already solved it.")
      }

      else if (text.contains("That's the right answer")) {
        val url = s"$baseUrl/$year/day/${if (part == 1) s"$day#part2" else s"${day + 1}"}"
        val res = Results.GetOne(part, date).getResult.toOption.get
        val newRes = os.read(Files.resultsFile).replace(res.raw, res.copy(submitted = true).raw)
        os.write.over(Files.resultsFile, newRes)
        Logging.success(s"${Logging.yellow("Correct")} answer!")
        Logging.success(s"Get the next problem at $url")
      }

      else {
        Logging.error("Something seems to have gone wrong when submitting the result.")
        Logging.error("Make sure your authentication is correctly configured by running 'aoc settings auth retry'.")
      }
      
    }
  }
}
