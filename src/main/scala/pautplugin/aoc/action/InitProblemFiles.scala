package pautplugin.aoc.action

import pautplugin.aoc._
import pautplugin.utils.Logging

import java.time.LocalDate

case class InitProblemFiles(name: String, date: LocalDate) extends Action with Date with AdventAuth {
  private val aoc = os.pwd / "src" / "main" / "scala" / "aoc"
  private val packages = s"package aoc.y$year.day$formattedDay"
  private val imports = "import problemutils.*, extensions.*"

  private def problem(part: Int) = 
   s"""|$packages
       |import paut.aoc.*
       |$imports
       |
       |object Part$part extends Problem($year, $day)($part)(???):
       |  def name = "$name - Part $part"
       |  def solve(data: List[String]) = ???
       |""".stripMargin

  private val utilities = 
   s"""|$packages
       |$imports
       |
       |def parse(data: List[String]) = ???
       |""".stripMargin
  
  private val testing = 
   s"""|$imports
       |import paut.aoc.Testing.read
       |
       |val primaryExampleData = read("examples", "$year", "$formattedDay-1")
       |val puzzleData = read("puzzles", "$year", "$formattedDay")
       |val data = primaryExampleData
       |""".stripMargin

  private def write(file: os.Path, content: String) = {
    val msg = s"File $file already exists, skipping..."
    Logging.fromBoolean(!os.exists(file), msg) {
      os.write.over(file, content, createFolders = true)
      Logging.info(s"Created file $file")
    }
  }
  
  private val folder = 
    aoc / year.toString / s"$formattedDay-${name.replaceAll(raw"\s+", "-").toLowerCase}"

  private def writeProblem(part: Int) = write(folder / s"Part$part.scala", problem(part))
  private def writePackage = write(folder / "package.scala", utilities)
  private def writeTesting = write(folder / "testing.worksheet.sc", testing)
  
  def execute = {
    Logging.fromEither(tokenValue) { _ =>
      writePackage
      writeProblem(1)
      writeProblem(2)
      writeTesting
      Data.Fetch(date).execute
      Data.AddExample(date).execute
    }
  }
}
