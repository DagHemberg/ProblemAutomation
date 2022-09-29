package paut.aoc

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter
import Console._

case class Result(
    year: Int,
    day: Int,
    part: Int,
    solution: String,
    time: Double,
    timestamp: LocalDateTime,
    submitted: Boolean
) {
  override def toString = {
    val from = s"AoC $year day $day - Part $part"
    val latest = s"Latest run: ${timestamp.format(DateTimeFormatter.ISO_DATE_TIME)}"
    val dur = s"Duration: ${(f"${YELLOW}${time}%.5fs${RESET}")}"
    val sol = s"Solution: $solution"
    val stat = s"Status: ${if (submitted) s"${GREEN}Submitted${RESET}" else s"${RED}Not submitted${RESET}"}"
    s"""|--- $from ---
        |$latest
        |$dur
        |$sol
        |$stat""".stripMargin
  }
  def raw = s"$year;$day;$part;$solution;$time;$timestamp;$submitted"
}

object Result {
  def parse(str: String): Result = {
    val parts = str.split(";").toList
    require(parts.length == 7, "Invalid format")
    Result(
      year = parts(0).toInt,
      day = parts(1).toInt,
      part = parts(2).toInt,
      solution = parts(3),
      time = parts(4).toDouble,
      timestamp = LocalDateTime.parse(parts(5), DateTimeFormatter.ISO_DATE_TIME),
      submitted = parts(6).toBoolean
    )
  }
}