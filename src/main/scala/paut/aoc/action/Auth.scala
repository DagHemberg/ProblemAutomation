package paut.aoc.action

import paut.aoc._
import paut.utils._, Logging._
import scala.util.{Try, Success, Failure}
import Files._

object Auth {
  case object GetSession extends Action with AdventAuth {
    def execute = Logging.fromOption(tokenValue, tokenMissingMsg) { token => 
      info(s"Currently authenticated as: ${yellow(username getOrElse "<no username found>")}")
    }
  }

  case class SetSession(sessionToken: String) extends Action with AdventAuth {
    def execute = {      
      info("Attempting to connect to Advent of Code servers...")
      
      os.write.over(tokenFile, sessionToken, createFolders = true)
      val response = AdventAPI.get(AdventAPI.baseUrl)
      if (response.isLeft) os.remove(tokenFile)

      Logging.fromEither(response) { doc =>
        val username = doc.select(".user").text.replaceAll(raw"\s+(\d{1,2}\*)", "")
        os.write.over(usernameFile, username, createFolders = true)
        success(s"Successfully authenticated as: ${yellow(username)}")
      }
    }
  }

  case object Reset extends Action {
    def execute = {
      os.remove(usernameFile)
      os.remove(tokenFile)
      success("Authentication reset.")
    }
  }

  case object Reattempt extends Action with AdventAuth {
    def execute = Logging.fromOption(tokenValue, tokenMissingMsg)(SetSession(_).execute)
  }
}