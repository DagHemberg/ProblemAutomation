package pautplugin.aoc
import java.time.LocalDate
import java.util.GregorianCalendar
import java.util.TimeZone

/** A trait that can be extended by Actions in order to provide easy access to the current date and year */
trait Date {
  val date: LocalDate
  val day = date.getDayOfMonth
  val formattedDay = f"${day}%02d"
  val year = date.getYear
}

/** Companion object for Date trait. Contains useful functions for getting the current dates and available years and dates within a year. */
object Date {  
  def from(day: Int, year: Option[Int] = None) = {
    LocalDate.of(year.getOrElse(defaultYear), 12, day)
  }
  
  def today = {
    new GregorianCalendar(TimeZone.getTimeZone("EST"))
      .toZonedDateTime()
      .toLocalDate()
  }

  def availableYears = {
    val currentYear = today.getYear
    2015 until currentYear + (if (today isBefore LocalDate.of(currentYear, 12, 1)) 0 else 1)
  }

  def availableDays = {
    val now = today
    1 to (if (defaultYear == now.getYear && now.getMonthValue == 12) now.getDayOfMonth else 25)
  }

  def defaultYear = Files
    .read(Files.defaultYearFile)
    .map(_.toInt)
    .getOrElse(availableYears.max)
}