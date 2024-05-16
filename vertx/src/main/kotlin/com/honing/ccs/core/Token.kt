package com.honing.ccs.core

data class Token(val accessToken: String, val expiresIn: Long) {
  override fun toString(): String {
    return "{ \"access-token\": \"$accessToken\", \"expiresIn\": $expiresIn}"
  }
}
