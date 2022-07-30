package problemautomation.adventofcode.actions

object Stats {
  case object Get extends Action {
    def execute = ???
  }

  case class Toggle(part: Int, day: Int, year: Option[Int]) extends Action {
    def execute = ???
  }
}