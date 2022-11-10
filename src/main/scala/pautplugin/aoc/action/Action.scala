package pautplugin.aoc.action

trait Action extends Doc {
  def execute: Unit
}

object EmptyAction extends Action {
  def execute = ()
  val doc = "Command not recognized."
}
