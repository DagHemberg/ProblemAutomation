package pautplugin.aoc.action

import java.time.LocalDate
import sys.process._
import pautplugin.utils._, Logging._
import pautplugin.aoc._

case class SubmitSolution(part: Int, date: LocalDate) extends Action with Date with AdventAuth {
  private val notCorrectMsg = "That's not the correct answer."

  private def too(msg: String) = s"Your answer is ${red(s"too $msg")}."
  private val tooHighMsg = too("high")
  private val tooLowMsg = too("low")

  private def wait(text: String) = {
    val r1 = "(?<=  Please wait )(.*)(?= before)".r.findFirstIn(text)
    val r2 = "(?<=  You have )(.*)(?= left to wait)".r.findFirstIn(text)
    val time = r1.orElse(r2).getOrElse("<N/A>")
    error(s"Please wait $time before submitting another answer.")
  }

  private def post = 
    Results.GetOne(part, date)
      .get
      .toRight(s"No result found for $year day $day, part $part")
      .flatMap { res => 
        if (res.submitted) Left(Results.alreadySubmittedMsg)
        else {
          AdventAPI.post(
            data = Map("level" -> part.toString, "answer" -> res.solution),
            url = AdventAPI.url(date.getYear, date.getDayOfMonth, "answer"), 
          )
        }
      }

  def execute = Logging.fromEither(post) { text =>
    if (text.contains("too recently")) {
      error("You submitted an answer too recently.")
      wait(text)
    }

    else if (text.contains("not the right answer")) {
      error(notCorrectMsg)
      if (text.contains("too high")) error(tooHighMsg)
      else if (text.contains("too low")) error(tooLowMsg)
      wait(text)
    }

    // shouldn't really be needed, but adding just in case
    else if (text.contains("You don't seem to be solving the right level")) {
      error("You don't seem to be solving the right level. Either you don't have access to this problem yet or you've already solved it.")
    } 

    else if (text.contains("That's the right answer")) {
      val url = s"${AdventAPI.baseUrl}/$year/day/${if (part == 1) s"$day#part2" else s"${day + 1}"}"
      val res = Results.GetOne(part, date).get.get
      val newRes = os.read(Files.resultsFile).replace(res.raw, res.copy(submitted = true).raw)
      os.write.over(Files.resultsFile, newRes)
      success(s"${yellow("Correct")} answer!")
      success(s"Get the next problem at $url")
    }
  }
}
