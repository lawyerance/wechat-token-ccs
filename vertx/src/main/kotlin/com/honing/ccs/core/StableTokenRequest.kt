package com.honing.ccs.core

import com.fasterxml.jackson.annotation.JsonProperty

data class StableTokenRequest(
  @get: JsonProperty("grant_type") @param: JsonProperty("grant_type") val grantType: String,
  @get: JsonProperty("appid") @param: JsonProperty("appid") val appid: String,
  @get: JsonProperty("secret") @param: JsonProperty("secret") val secret: String
)
