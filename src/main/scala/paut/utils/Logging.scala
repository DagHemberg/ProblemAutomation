package paut.utils

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
    * @see [[fromOption]]
    * @see [[fromBoolean]]
    */
  def fromEither[A](either: Either[String, A])(f: A => Unit) = either match {
    case Left(msg) => error(msg)
    case Right(a) => f(a)
  }

  /** Produces an effect or an error message based on an [[scala.Option]]. 
    * - If this object is a [[scala.None]], it will print the supplied error 
    * message. 
    * - If this object is a [[scala.Some]], the supplied function will be 
    * applied to the value of the Some object.
    * 
    * This is equivalent to:
    * ```
    * val msg = /** ... */ ???
    * option match {
    *   case None => Logging.error(msg)
    *   case Some(x) => f(x)
    * }
    * ```
    * @param option the option to match
    * @param f the procedure to apply to the value of the Some object
    * @see [[fromEither]]
    * @see [[fromBoolean]]
    */
  def fromOption[A](option: Option[A], msg: String)(f: A => Unit) = option match {
    case None => error(msg)
    case Some(a) => f(a)
  }

  /** Produces an effect depending on the value of a boolean expression. 
    * - If the expression is false, it will print the supplied error message. 
    * - If the expression is true, the supplied block will be run.
    * 
    * This is equivalent to:
    * ```
    * val msg = /* ... */ ???
    * if (bool) block
    * else Logging.error(msg)
    * ```
    * @param bool the expression to check
    * @param block the procedure to run
    * @see [[fromEither]]
    * @see [[fromOption]]
    */
  def fromBoolean(bool: Boolean, msg: String)(block: => Unit) = {
    if (bool) block
    else error(msg)
  }
}