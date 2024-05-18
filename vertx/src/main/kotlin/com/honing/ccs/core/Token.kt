package com.honing.ccs.core

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class Token(
  @get: JsonProperty("access_token") @param: JsonProperty("access_token") val accessToken: String?,
  @get: JsonProperty("expires_in") @param: JsonProperty("expires_in") val expiresIn: Long?
) : Serializable {


  override fun toString(): String {
    return """{"access_token": "$accessToken", "expires_in": $expiresIn}"""
  }
}
