package pautplugin.aoc.action

import pautplugin.aoc._
import pautplugin.utils._

import java.time.LocalDate
import scala.collection.compat.immutable.LazyList
import scala.util.Failure
import scala.util.Try
import scala.util.chaining._

object Data {

  private def call(args: os.Shellable*) = Try {
    os.proc(args: _*).call(stderr = os.Pipe)
  }

  sealed trait OS
  case object Windows extends OS
  case object MacOS extends OS
  case object Linux extends OS
  case object WSL extends OS
  case object Other extends OS

  def determineOS: OS = sys.props("os.name") match {
    case w if w.contains("Windows") => Windows
    case "Mac OS X" => MacOS
    case "Linux" => {
      if (sys.env.contains("WSL_DISTRO_NAME")) WSL
      else Linux
    }
    case other => Other
  }

  private type TCR = Try[os.CommandResult]

  private def open(path: os.Path, recover: TCR => TCR) = {
    val args: Seq[os.Shellable] = Seq("-NoProfile", "-Command", "Start-Process", path)
    determineOS match {
      case MacOS => 
        call("open", path)

      case Windows => 
        call("pwsh.exe", args)
          .recoverWith { case _ => call("powershell.exe", args) }
      
      case Linux => 
        call("xdg-open", path)
          .pipe(recover)
      
      case WSL => 
        call("pwsh.exe", args)
          .recoverWith { case _ => call("powershell.exe", args) }
          .recoverWith { case _ => call("xdg-open", path) }
          .pipe(recover)
      
      case Other => {
        Logging.error("Could not determine your operating system.")
        Failure(new Exception)
      }
    }
  }

  private def openFolder(path: os.Path) = open(path, identity)
  private def openFile(path: os.Path) = open(path, _
    .recoverWith { case _ => call("$VISUAL", path) }
    .recoverWith { case _ => call("$EDITOR", path) }
  )

  case object OpenFolder extends Action {
    val doc = 
      """|Opens the ~/.paut folder, where data, settings and results are stored.
         |
         |# USAGE
         |'aoc data openFolder'
         |""".stripMargin

    def execute = openFolder(Files.wd).failed.foreach { _ => 
      Logging.error(s"Could not open ${Files.wd}.")
    }
  }

  case class FetchInput(date: LocalDate) extends Action with Date with AdventAuth {  
    val doc = 
      s"""|Fetches the input data from a specific day of Advent of Code.
          |
          |# USAGE
          |'aoc data fetchInput <day> [year]'
          |
          |# NOTES
          |- 'aoc init' automatically runs this command, meaning this command should only be used if a problem occurs when downloading the data.
          |${Doc.authNote}
          |${Doc.dayYearNote}
          |${Doc.todayNote}
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
          |'aoc data openInput <day> [year]'
          |
          |# NOTES
          |${Doc.dayYearNote}
          |${Doc.todayNote}
          |""".stripMargin

    def execute = {
      val file = Files.puzzles / year.toString / s"$formattedDay.txt"
      if (os.exists(file)) {
        openFile(file)
          .failed
          .foreach(_ => Logging.error(s"Could not open ${file.relativeTo(os.pwd)}."))
      } else {
        Logging.error(s"Input file $year, day $day does not exist.")
      }
    }
  }

  case class AddExample(date: LocalDate) extends Action with Date { 
    val doc = 
      s"""|Adds a new example file for a specific day of Advent of Code.
          |
          |# USAGE
          |'aoc data addExample <day> [year]'
          |
          |# NOTES
          |${Doc.dayYearNote}
          |${Doc.todayNote}
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
          |'aoc data openExample <number> <day> [year]'
          |
          |# NOTES
          |${Doc.dayYearNote}
          |${Doc.todayNote}
          |""".stripMargin

    def execute = {
      val file = path(part, formattedDay, year)
      if (os.exists(file)) {
        openFile(file)
          .failed
          .foreach(_ => Logging.error(s"Could not open ${file.relativeTo(os.pwd)}."))
      } else {
        Logging.error(s"Example file $part from year $year, day $day does not exist.")
      }
    }
  }

  case class CreateFiles(name: String, date: LocalDate) extends Action with Date {
    val doc = 
      s"""|Creates skeleton files for solving the problems from a specific day of Advent of Code.
          |
          |# USAGE
          |'aoc data createFiles "<name>" <day> [year]'
          |
          |# NOTES
          |- 'aoc init' automatically runs this command, meaning this command should only be used if a problem occurs when initializing the problem files.
          |${Doc.authNote}
          |${Doc.todayNote}
          |${Doc.dayYearNote}
          |""".stripMargin
    
    private val aoc = os.pwd / "src" / "main" / "scala" / "aoc"
    private val packages = s"package aoc.y$year.day$formattedDay"
    private val imports = "import problemutils.*, extensions.*"

    private def problem(part: Int) = 
    s"""|$packages
        |import paut.aoc.*
        |$imports
        |
        |object Part$part extends Problem($day, $year, Pt$part)(???):
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
      val relative = file.relativeTo(os.pwd)
      if (!os.exists(file)) {
        os.write.over(file, content, createFolders = true)
        Logging.info(s"Created file $relative")
      } else {
        Logging.error(s"File $relative already exists, skipping...")
      }
    }
    
    private val folder = 
      aoc / year.toString / s"$formattedDay-${name.replaceAll(raw"\s+", "-").toLowerCase}"

    private def writeProblem(part: Int) = write(folder / s"Part$part.scala", problem(part))
    private def writePackage = write(folder / "package.scala", common)
    private def writeTesting = write(folder / "testing.worksheet.sc", testing)
    
    def execute = {
      writePackage
      writeProblem(1)
      writeProblem(2)
      writeTesting
    }
  }
}
