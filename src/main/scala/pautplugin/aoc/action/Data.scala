package pautplugin.aoc.action

import pautplugin.aoc._
import pautplugin.utils._

import java.time.LocalDate
import scala.collection.compat.immutable.LazyList
import scala.util.Try
import scala.util.Failure

object Data {

  private val env = sys.env
  private def call(cmd: String, path: os.Path) = os.proc(cmd, path).call()

  private def open(path: os.Path) = {
    Try(call("open", path))
      .recoverWith { case _ => Try(call("xdg-open", path)) }
  }

  private def openFile(path: os.Path) = {
    open(path)
      .recoverWith { case _ => Try(call(env("VISUAL"), path)) }
      .recoverWith { case _ => Try(call(env("EDITOR"), path)) }
      .failed.foreach(_ => Logging.error(
        s"Could not open $path. Make sure either 'xdg-open' is installed, or that your $$VISUAL or $$EDITOR environment variables are set."
      ))
  }

  case object OpenFolder extends Action {
    val doc = 
      """|Opens the ~/.paut folder, where data, settings and results are stored.
         |
         |# USAGE
         |'aoc data openFolder'
         |""".stripMargin

    def execute = {
      open(Files.wd).failed.foreach(_ => Logging.error(
        s"Could not open ${Files.wd}. Make sure 'xdg-open' is installed."
      ))
    }
  }

  case class FetchInput(date: LocalDate) extends Action with Date with AdventAuth {  
    val doc = 
      s"""|Fetches the input data from a specific day of Advent of Code.
          |
          |# USAGE
          |'aoc data fetchInput <today | <day> [year]>'
          |
          |# NOTES
          |- 'aoc data initProblem' automatically runs this command, meaning this command should only be used if a problem occurs when downloading the data.
          |${Doc.auth}
          |${Doc.dayYear}
          |${Doc.today}
          |""".stripMargin

    private val puzzleFile = Files.puzzles / year.toString / s"$formattedDay.txt"
  
    def execute = {
      val response = {
        if (os.exists(puzzleFile)) Left("Input data for this day already exists.")
        else {
          Logging.info(s"Fetching data for $year, day $day...")
          Request.get(problemUrl(year, day, "input"))
        }
      }
      
      Logging.fromEither(response) { data =>
        os.write.over(puzzleFile, data, createFolders = true)
      }
    }
  }

  private def path(part: Int, day: String, year: Int) = Files.examples / year.toString / s"$day-$part.txt"

  case class OpenInput(date: LocalDate) extends Action with Date {
    val doc = 
      s"""|Opens the input file for a specific day of Advent of Code.
          |
          |# USAGE
          |'aoc data openInput <today | <day> [year]>'
          |
          |# NOTES
          |${Doc.dayYear}
          |${Doc.today}
          |""".stripMargin

    def execute = {
      val file = Files.puzzles / year.toString / s"$formattedDay.txt"
      val exists = os.exists(file)
      Logging.fromBoolean(exists, s"Example file from year $year, day $day does not exist.") { 
        openFile(file)
      }
    }
  }

  case class AddExample(date: LocalDate) extends Action with Date { 
    val doc = 
      s"""|Adds a new example file for a specific day of Advent of Code.
          |
          |# USAGE
          |'aoc data addExample <today | <day> [year]>'
          |
          |# NOTES
          |${Doc.dayYear}
          |${Doc.today}
          |""".stripMargin

    def execute = {
      val yearPath = Files.examples / year.toString
      if (!os.exists(yearPath)) os.makeDir.all(yearPath)
      val extract = raw"$formattedDay-(\d+)".r
      val parts = os
        .list(yearPath)
        .map(_.baseName)
        .collect { case extract(part) => part.toInt }
      val newest = LazyList.from(1).filterNot(parts.contains).head
      val examplePath = path(newest, formattedDay, year)
      os.write.over(examplePath, "Remove this line and paste your example data here!", createFolders = true)
      openFile(examplePath)
    }
  }

  case class OpenExample(part: Int, date: LocalDate) extends Action with Date {
    val doc = 
      s"""|Opens the example file for a specific day of Advent of Code.
          |
          |# USAGE
          |'aoc data openExample <number> <today | <day> [year]>'
          |
          |# NOTES
          |${Doc.dayYear}
          |${Doc.today}
          |""".stripMargin

    def execute = {
      val exists = os.exists(path(part, formattedDay, year))
      val msg = s"Example file $part from year $year, day $day does not exist."
      Logging.fromBoolean(exists, msg) {
        openFile(path(part, formattedDay, year))
      }
    }
  }

  case class InitProblem(name: String, date: LocalDate) extends Action with Date with AdventAuth {
    val doc =       
      s"""|Downloads and creates the files necessary to solve a specific problem from Advent of Code.
          |
          |# USAGE
          |'aoc data initProblem "<name>" <today | <day> [year]>'
          |
          |# NOTES
          |${Doc.auth}
          |${Doc.today}
          |${Doc.dayYear}
          |""".stripMargin
    
    private val aoc = os.pwd / "src" / "main" / "scala" / "aoc"
    private val packages = s"package aoc.y$year.day$formattedDay"
    private val imports = "import problemutils.*, extensions.*"

    private def problem(part: Int) = 
    s"""|$packages
        |import paut.aoc.*
        |$imports
        |
        |object Part$part extends Problem($day, $year)($part)(???):
        |  def name = "$name - Part $part"
        |  def solve(data: List[String]) = ???
        |""".stripMargin

    private val common = 
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
    private def writePackage = write(folder / "package.scala", common)
    private def writeTesting = write(folder / "testing.worksheet.sc", testing)
    
    def execute = Logging.fromEither(tokenValue) { _ =>
      writePackage
      writeProblem(1)
      writeProblem(2)
      writeTesting
      Data.FetchInput(date).execute
      Data.AddExample(date).execute
    }
  }
}
