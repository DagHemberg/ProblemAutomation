package pautplugin.aoc

import pautplugin.utils.Authentication

trait AdventAuth extends Authentication {
  def tokenValue = Files.token.toRight(left = tokenMissingMsg)
  val tokenName = "session"
  val userAgent = "https://github.com/daghemberg/pAut.g8 by dag.hemberg@gmail.com"
  val tokenMissingMsg = 
    "No authentication token found. Please run 'aoc auth set <token>' first."
}