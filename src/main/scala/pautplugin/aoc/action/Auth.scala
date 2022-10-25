package pautplugin.aoc.action

import pautplugin.aoc._
import pautplugin.utils._

import scala.util.Failure
import scala.util.Success
import scala.util.Try

import Logging._
import Files._

object Auth {
  case object GetSession extends Action with AdventAuth {
    val doc = 
      """|If successfully authenticated, prints the username of the currently authenticated user.
         |
         |# USAGE
         |'aoc auth get'
         |""".stripMargin
    
    def execute = Logging.fromEither(tokenValue) { token => 
      info(s"Currently authenticated as: ${yellow(username getOrElse "<no username found>")}")
    }
  }

  case class SetSession(sessionToken: String) extends Action with AdventAuth {
    val doc = 
      """|Attempts to authenticate with the given token.
         |
         |# USAGE
         |'aoc auth set <token>'
         |""".stripMargin

    def execute = {      
      info("Attempting to connect to Advent of Code servers...")
      
      os.write.over(tokenFile, sessionToken, createFolders = true)
      val response = Request
        .get(baseUrl)
        .flatMap { data => 
          """<div class=\"user\">(.+?)\s?(<span class=\"star-count\">|<\/div>)"""
            .r.findFirstMatchIn(data)
            .toRight(left = Request.requestFailedMsg(baseUrl))
            .map(_.group(1))
        }

      if (response.isLeft) os.remove(tokenFile)

      Logging.fromEither(response) { username =>
        os.write.over(usernameFile, username, createFolders = true)
        success(s"Successfully authenticated as: ${yellow(username)}")
      }
    }
  }

  case object Reset extends Action {
    val doc = 
      """|Removes the current session token and username.
         |
         |# USAGE
         |'aoc auth reset'
         |""".stripMargin
    
    def execute = {
      os.remove(usernameFile)
      os.remove(tokenFile)
      success("Authentication reset.")
    }
  }

  case object Reattempt extends Action with AdventAuth {
    val doc = 
      """|Attempts to reauthenticate with the current session token. 
         |If unsuccessful, the token is removed.
         |
         |# USAGE
         |'aoc auth retry'
         |""".stripMargin

    def execute = Logging.fromEither(tokenValue)(SetSession(_).execute)
  }
}
