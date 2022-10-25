package pautplugin.aoc.action

case class Help(d: Doc) extends Action {
  def execute = println(d.doc)
  val doc = 
    """|Prints an explanation on how to use the given command.
       |""".stripMargin
}