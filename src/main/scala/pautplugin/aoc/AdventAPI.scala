package pautplugin.aoc

import pautplugin.utils.Request

object AdventAPI extends Request {
  val baseUrl = "https://adventofcode.com" 
  def url(year: Int, day: Int, input: String) = s"$baseUrl/$year/day/$day/$input"
}