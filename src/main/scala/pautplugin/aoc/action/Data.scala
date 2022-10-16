package pautplugin.aoc.action

import pautplugin.aoc._
import pautplugin.utils._

import java.time.LocalDate
import scala.collection.compat.immutable.LazyList

object Data {
  case object OpenFolder extends Action {
    def execute: Unit = os.proc("open", Files.wd).call()
  }

  case class Fetch(date: LocalDate) extends Action with Date with AdventAuth {  
    private val puzzleFile = Files.puzzles / year.toString / s"$day.txt"
  
    def execute: Unit = {
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

  case class OpenExample(part: Int, date: LocalDate) extends Action with Date {
    def execute = {
      val exists = os.exists(path(part, formattedDay, year))
      val msg = s"Example file $part from year $year, day $day does not exist"
      Logging.fromBoolean(exists, msg) {
        os.proc("open", path(part, formattedDay, year)).call()
      }
    }
  }

  case class AddExample(date: LocalDate) extends Action with Date { 
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
      os.write.over(examplePath, "Paste your example data here!", createFolders = true)
      os.proc("open", examplePath).call()
    }
  }
}
