package pautplugin.aoc

import pautplugin.utils.Authentication

trait AdventAuth extends Authentication {
  def tokenValue = Files.token
  val tokenName = "session"
  val userAgent = "paut-aoc v0.1"
  val tokenMissingMsg = 
    "No authentication token found. Please run 'aoc settings auth set <token>' first."
}