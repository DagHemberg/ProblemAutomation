package pautplugin.aoc.action

trait Action extends Doc {
  def execute: Unit
}

object EmptyAction extends Action {
  def execute: Unit = ()
  val doc: String = "Command not recognized."
}