package pautplugin.aoc.action

case class Help(d: Doc) extends Action {
  def execute = println(d.doc)
  val doc = 
    """|Performs various actions related to Advent of Code.
       |
       |# USAGE
       |
       |'aoc help'                                                  Prints this message
       |  'aoc help <action>'                                       Prints the documentation for a specific action
       |
       |'aoc data <action>'                                         Manages input data for problems
       |  'aoc data openFolder'                                     Opens the ~/.paut config folder
       |  'aoc data initProblem "<name>" <today | <day> [year]>'    Initializes a problem
       |  'aoc data openExample <number> <today | <day> [year]>'    Opens the example file for a problem
       |  'aoc data addExample <today | <day> [year]>'              Adds an example file for a problem
       |  'aoc data fetchManual <today | <day> [year]>'             Manually fetches the input data for a problem
       |
       |'aoc auth <action>'                                         Manages the authentication token
       |  'aoc auth set <token>'                                    Sets the authentication token
       |  'aoc auth get'                                            Prints AoC username
       |  'aoc auth reset'                                          Resets the authentication token
       |  'aoc auth reattempt'                                      Attempts to reauthenticate
       |
       |'aoc defaultYear <action>'                                  Manages the default year
       |  'aoc defaultYear set <year>'                              Sets the default year
       |  'aoc defaultYear get'                                     Prints the default year
       |  'aoc defaultYear reset'                                   Resets the default year
       |
       |'aoc results <action>'                                      Manages solutions to solved problems
       |  'aoc results viewAll'                                     Prints all results
       |  'aoc results get <part> <today | <day> [year]>'           Prints the result for a specific problem
       |  'aoc results submit <part> <today | <day> [year]>'        Submits the result for a specific problem
       |
       |""".stripMargin
}