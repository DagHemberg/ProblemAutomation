package paut.aoc

import paut.utils.Request

object API extends Request {
  val baseUrl = "https://adventofcode.com" 
  def url(year: Int, day: Int, input: String) = s"$baseUrl/$year/day/$day/$input"
}