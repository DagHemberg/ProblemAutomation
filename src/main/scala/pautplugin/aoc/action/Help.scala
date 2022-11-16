package pautplugin.aoc.action

case class Help(d: Doc) extends Action {
  def execute = println(d.doc)
  val doc = 
    s"""|Performs various actions related to Advent of Code.
        |
        |# USAGE
        |
        |'aoc help'                                        Prints this message
        |  'aoc help <action>'                             Prints the documentation for a specific action
        |
        |'aoc init "<name>" <day> [year]'                  Creates and downloads problem / input files.
        |
        |'aoc data <action>'                               Manages input data for problems
        |  'aoc data openFolder'                           Opens the ~/.paut config folder
        |  'aoc data fetchInput <day> [year]'              Manually fetches the input data for a problem
        |  'aoc data openInput <day> [year]'               Opens the input data for a problem
        |  'aoc data addExample <day> [year]'              Adds an example file for a problem
        |  'aoc data openExample <number> <day> [year]'    Opens the example file for a problem
        |  'aoc data createFiles "<name>" <day> [year]'    Creates files for solving the day's problems.
        |
        |'aoc auth <action>'                               Manages the authentication token
        |  'aoc auth set <token>'                          Sets the authentication token
        |  'aoc auth get'                                  Prints AoC username
        |  'aoc auth reset'                                Resets the authentication token
        |  'aoc auth retry'                                Attempts to reauthenticate
        |
        |'aoc defaultYear <action>'                        Manages the default year
        |  'aoc defaultYear set <year>'                    Sets the default year
        |  'aoc defaultYear get'                           Prints the default year
        |  'aoc defaultYear reset'                         Resets the default year
        |
        |'aoc results <action>'                            Manages solutions to solved problems
        |  'aoc results viewAll'                           Prints all results
        |  'aoc results get <part> <day> [year]'           Prints the result for a specific problem
        |  'aoc results submit <part> <day> [year]'        Submits the result for a specific problem
        |
        |# NOTES
        |${Doc.todayNote}
        |
        |""".stripMargin
}