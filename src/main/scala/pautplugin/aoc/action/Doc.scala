package pautplugin.aoc.action

import java.time.LocalDate

trait Doc {
  def doc: String
}

object Doc {
  val part = "- <part> must be 1 or 2"
  val auth = "- Requires a valid authentication token. See 'aoc help auth' for more information."
  val today = "- The 'today' keyword can only be used if a new problem was created on this day."
  val dayYear = """|- <day> must be a number between 1 and 25
                   |- [year] is optional, but defaults to the default year. For more information, see 'aoc help setYear'""".stripMargin
  val defaultYear = 
    "- When not set manually, the year defaults to the latest year with available problems, which is updated dynamically"

  val n = LocalDate.now()

  def apply(name: String) = allDocs(name)

  val allDocs: Map[String, Action] = Map(
    "help" -> Help(EmptyAction),

    // auth
    "getAuth" -> Auth.GetSession,
    "setAuth" -> Auth.SetSession(""),
    "resetAuth" -> Auth.Reset,
    "reattemptAuth" -> Auth.Reattempt,

    // defaultYear
    "setDefaultYear" -> DefaultYear.SetYear(0),
    "getDefaultYear" -> DefaultYear.Get,
    "resetDefaultYear" -> DefaultYear.Reset,

    // results
    "getResult" -> Results.GetOne(0, n),
    "viewAllResults" -> Results.GetAll,
    "submitResult" -> Results.Submit(0, n),
    
    // data
    "openDataFolder" -> Data.OpenFolder,
    "fetchInput" -> Data.FetchInput(n),
    "openInput" -> Data.OpenInput(n),
    "addExample" -> Data.AddExample(n),
    "openExample" -> Data.OpenExample(0, n),
    "initProblem" -> Data.InitProblem("", n),
  )
}