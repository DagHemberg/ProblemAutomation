package pautplugin

package object aoc {
  val baseUrl = "https://adventofcode.com"
  def problemUrl(year: Int, day: Int, extra: String) = s"$baseUrl/$year/day/$day/$extra"
}
