package pautplugin.utils

import sttp.client3._

import scala.util.Try
import scala.util.chaining._

object Request {
  private val backend = HttpURLConnectionBackend()

  private def requestFailedMsg(url: String) = 
    s"Could not connect to $url. Make sure your authentication is correctly configured."

  private type ESS = Either[String, String]
  private type Session = RequestT[Empty, ESS, Any]
  private type Connection = Request[ESS, Any]

  private def attempt(connect: Session => Connection, url: String)(implicit auth: Authentication) = auth
    .tokenValue
    .flatMap { token => basicRequest
      .cookie(auth.tokenName, token)
      .headers(Map("User-Agent" -> auth.userAgent))
      .pipe(connect)
      .pipe { connection => Try(connection.send(backend).body)
        .getOrElse(Left(requestFailedMsg(url)))
      }
    }

  def get(url: String)(implicit auth: Authentication) = 
    attempt(_.get(uri"$url"), url)

  def post(url: String, data: Map[String, String])(implicit auth: Authentication) =
    attempt(_.body(data).post(uri"$url"), url)
}