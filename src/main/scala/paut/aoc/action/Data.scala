package paut.aoc.action

import java.time.LocalDate
import paut.aoc._
import paut.utils._
import scala.collection.compat.immutable.LazyList

object Data {
  case object OpenFolder extends Action {
    def execute: Unit = os.proc("open", Files.wd).call()
  }

  case class Fetch(date: LocalDate) extends Action with Date with AdventAuth {  
    private val puzzle = Files.puzzles / year.toString / s"$day.txt"
  
    def execute: Unit = {
      val response = {
        if (os.exists(puzzle))
          Left("Input data for this day already exists.")
        else {
          Logging.info(s"Fetching data for $year, day $day...")
          API.get(API.url(year, day, "input"))
        }
      }
      
      Logging.fromEither(response) { doc =>
        os.write.over(puzzle, doc.text, createFolders = true)
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
      val extract = raw"$formattedDay-(\d+)".r
      val parts = os
        .list(Files.examples / year.toString)
        .map(_.baseName)
        .collect { case extract(part) => part.toInt }
      val newest = LazyList.from(1).filterNot(parts.contains).head
      val examplePath = path(newest, formattedDay, year)
      os.write.over(examplePath, "", createFolders = true)
      os.proc("open", examplePath).call()
    }
  }
}
