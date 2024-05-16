package com.honing.css.spring.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.io.Serializable

@JsonPropertyOrder(value = ["grantType"])
data class StableTokenDTO(
  @JsonProperty(value = "grant_type") var grantType: String = "client_credential",
  var appid: String,
  var secret: String,
  @JsonProperty(value = "force_refresh") var forceRefresh: Boolean
) : Serializable
