package pautplugin.aoc.action

trait Action extends Doc {
  def execute: Unit
}

object EmptyAction extends Action {
  val doc = "Command not recognized."
  def execute = ()
}
