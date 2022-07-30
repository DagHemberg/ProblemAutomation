package problemautomation.adventofcode.actions

object Auth {
  case class Set(cookie: String) extends Action {
    def execute = ???
  }

  case object Get extends Action {
    def execute = ???
  }

  case object Retry extends Action {
    def execute = ???
  }

  case object Reset extends Action {
    def execute = ???
  }
}