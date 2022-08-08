package paut.aoc

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter
import paut.utils.Logging

case class Result(
    year: Int,
    day: Int,
    part: Int,
    timestamp: LocalDateTime,
    solution: String,
    time: Double,
    submitted: Boolean
) {
  override def toString = {
    val from = s"AoC $year day $day - Part $part"
    val latest = s"Latest run: ${timestamp.format(DateTimeFormatter.ISO_DATE_TIME)}"
    val dur = s"Duration: ${Logging.yellow(f"${time}%.5fs")}"
    val sol = s"Solution: $solution"
    val stat = s"Status: ${if (submitted) Logging.green("Submitted") else Logging.red("Not Submitted")}"
    s"""|--- $from ---
        |$latest
        |$dur
        |$sol
        |$stat""".stripMargin
  }
  def raw = s"$year;$day;$part;$timestamp;$solution;$time;$submitted"
}

object Result {
  def parse(str: String): Result = {
    val parts = str.split(";").toList
    require(parts.length == 7, "Invalid format")
    Result(
      parts(0).toInt,
      parts(1).toInt,
      parts(2).toInt,
      LocalDateTime.parse(parts(3), DateTimeFormatter.ISO_DATE_TIME),
      parts(4),
      parts(5).toDouble,
      parts(6).toBoolean
    )
  }
}