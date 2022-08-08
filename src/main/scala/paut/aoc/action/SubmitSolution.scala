package paut.aoc.action

import java.time.LocalDate
import paut.utils._, Logging._
import paut.aoc._

case class SubmitSolution(part: Int, date: LocalDate) extends Action with Date with AdventAuth {
  private def too(msg: String) = s"That's not the correct answer; your answer is ${red(s"too $msg")}."
  private val tooHighMsg = too("high")
  private val tooLowMsg = too("low")

  private def post = 
    Results.GetOne(part, date)
      .get
      .toRight(s"No result found for $year day $day, part $part")
      .flatMap { res => 
        if (res.submitted) Left("This solution has already been submitted and been verified to be correct.")
        else Right(res)
      }
      .flatMap { res =>
        API.post(
          // not sure if this actually works lmao
          API.url(date.getYear, date.getDayOfMonth, "answer"), 
          data = Map("level" -> part.toString, "answer" -> res.solution),
        )
      }

  def execute = Logging.fromEither(post) { doc =>
    val text = doc.body.text
    
    println(text)
    
    if (text.contains("too recently")) error(text)
    else if (text.contains("too high")) error(tooHighMsg)
    else if (text.contains("too low")) error(tooLowMsg)
    else if (text.contains("correct")) {
      val url = s"${API.baseUrl}/$year/day/${if (part == 1) s"$day#part2" else s"${day + 1}"}"
      success(s"${yellow("Correct")} answer!")
      success(s"Get the next problem at $url")
    }
  }
}
