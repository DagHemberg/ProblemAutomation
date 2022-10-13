package pautplugin.utils

/** A trait used for authenticating to session-based websites. 
  * Any class, object, function or value that makes use of the `get` or `post` methods
  * in [[pautplugin.utils.Request]] should either implement this trait and override the abstract 
  * methods and values directly, or implement a subtrait of this trait that overrides the 
  * abstract methods.
  */
trait Authentication {
  implicit val auth = this

  def tokenValue: Option[String]
  val tokenName: String
  val userAgent: String
  val tokenMissingMsg: String
}