package pautplugin.aoc.action

import java.time.LocalDate
import sys.process._
import pautplugin.utils._, Logging._
import pautplugin.aoc._

case class SubmitSolution(part: Int, date: LocalDate) extends Action with Date with AdventAuth {
  private val notCorrectMsg = "That's not the correct answer"

  private def too(msg: String) = s"$notCorrectMsg; your answer is ${red(s"too $msg")}."
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

          // temp until i figure out why jsoup doesn't work
          // auth.tokenValue.toRight(auth.tokenMissingMsg) flatMap { token =>
          //   val data = s"-d 'level=$part&answer=${res.solution}'"
          //   val userAgent = s"-A '${auth.userAgent}'"
          //   val url = AdventAPI.url(date.getYear, date.getDayOfMonth, "answer")
          //   val session = s"--cookie 'session=$token'"
          //   val cmd = s"curl --silent -X POST $userAgent $session $data $url"
          //   Right(s"""bash -c "$cmd"""".!!)
          // }
        }
      }

  def execute = Logging.fromEither(post) { text =>
    if (text.contains("too recently")) {
      wait(text)
    }
    else if (text.contains("not the right answer")) {
      error(notCorrectMsg)
      wait(text)
    }
    else if (text.contains("too high")) {
      error(tooHighMsg)
      wait(text)
    }
    else if (text.contains("too low")) {
      error(tooLowMsg)
      wait(text)  
    }
    else if (text.contains("correct")) {
      val url = s"${AdventAPI.baseUrl}/$year/day/${if (part == 1) s"$day#part2" else s"${day + 1}"}"
      success(s"${yellow("Correct")} answer!")
      success(s"Get the next problem at $url")
    }
  }
}
