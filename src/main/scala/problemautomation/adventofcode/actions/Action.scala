package problemautomation.adventofcode.actions

trait Action {
  def execute: Unit
}

case object Test extends Action {
  def execute = ???
}