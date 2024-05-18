package com.honing.ccs.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vertx.core.json.Json
import org.junit.jupiter.api.Test

class TokenTest {
  val mapper = ObjectMapper().registerModule(
    KotlinModule.Builder().build()
  )

  @Test
  fun writeValueAsString() {
    val string = mapper.writeValueAsString(Token("123", 10L))
    println(string)

    println("Json.encodeValue: " + Json.encodePrettily(Token("123", 10L)))
  }


  @Test
  fun readValue() {
    val s =
      """{"access_token":"80_-I4NJM_4wojj_cJgoqJN4QzUBe80oRKizzJEvr5-_dCyXovftR2psmJwvRy7fobKZc7B0L7cClGc4IJn_mmyuvtKbHSOIlFGv3ZBlLPTbE8yLKR4MdNF4d4UCccMSJeAHADJA","expires_in":7200} """

    val token: Token = mapper.readValue(
      s, Token::class.java
    )
    println(token)

    println("Json.decodeValue: " + Json.decodeValue(s, Token::class.java))
  }
}
