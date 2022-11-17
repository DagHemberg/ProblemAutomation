package pautplugin.utils

import scala.Console._

object Logging {
  def info(msg: String) = println(s"[info] $msg")
  def error(msg: String) = println(s"[${red("error")}] $msg")
  def warn(msg: String) = println(s"[${yellow("warn")}] $msg")
  def success(msg: String) = println(s"[${green("success")}] $msg")

  def color(str: String, COL: String) = s"${COL}$str${RESET}"

  def green(str: String) = color(str, GREEN)
  def red(str: String) = color(str, RED)
  def yellow(str: String) = color(str, YELLOW)
  def blue(str: String) = color(str, BLUE)
  def magenta(str: String) = color(str, MAGENTA)
  def cyan(str: String) = color(str, CYAN)
  def white(str: String) = color(str, WHITE)
  def black(str: String) = color(str, BLACK)

  /** Produces an effect or an error message based on an [[scala.Either]].
    * - If this object is a [[scala.Left]], it will print the error message 
    * present in the object. 
    * - If this object is a [[scala.Right]], the supplied function will be 
    * applied to the value of the object.
    * 
    * This is equivalent to:
    * {{{
    * either match {
    *   case Left(msg) => Logging.error(msg)
    *   case Right(x) => f(x)
    * }
    * }}}
    * @param either the either to match
    * @param f the procedure to apply to the value of the Right object
    */
  def fromEither[A](either: Either[String, A])(f: A => Unit) = either match {
    case Left(msg) => error(msg)
    case Right(a) => f(a)
  }
}