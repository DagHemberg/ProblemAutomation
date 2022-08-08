package paut.utils

import org.jsoup._, nodes.Document
import collection.JavaConverters._
import scala.util.{Try, Success, Failure}

abstract class Request {
  val baseUrl: String
  val notFoundMsg = "Error 404: Page not found"
  
  private def requestFailedMsg(url: String) = 
    s"Could not connect to $url. Make sure your authentication is correctly configured."

  private def connect(url: String, auth: Authentication) = auth
    .tokenValue
    .toRight(auth.tokenMissingMsg)
    .map { token => Jsoup
      .connect(url)
      .cookie(auth.tokenName, token)
      .userAgent(auth.userAgent)
    }

  private def attempt(url: String, auth: Authentication)(f: Connection => Document) = {
    connect(url, auth) flatMap { connection =>
      Try(f(connection)) match {
        case Success(doc) => Right(doc)
        case Failure(http: HttpStatusException) => 
          if (http.getStatusCode == 404) Left(notFoundMsg)
          else Left(requestFailedMsg(url))
        case Failure(other) => Left(s"??? idk fam \n\n${other.getStackTrace}")
      }
    }
  }

  def get(url: String)(implicit auth: Authentication) =
    attempt(url, auth)(_.get)

  // doesnt work, not sure why
  def post(url: String, data: Map[String, String])(implicit auth: Authentication) =
    attempt(url, auth)(_.data(data.asJava).post)
}
