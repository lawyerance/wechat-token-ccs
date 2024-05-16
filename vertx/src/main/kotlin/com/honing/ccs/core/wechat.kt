package com.honing.ccs.core

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.uritemplate.UriTemplate
import java.util.concurrent.TimeUnit



val webClientOptions: WebClientOptions =
  WebClientOptions().setSsl(true).setTrustAll(true).setDefaultPort(443).setKeepAlive(true);

val cache: Cache<String, Token> = Caffeine.newBuilder().expireAfterWrite(7000L, TimeUnit.SECONDS).build();

fun token(vertx: io.vertx.core.Vertx, appId: String, secret: String): Token? {

  val result: Token? = cache.getIfPresent(appId);
  if (result == null) {
    val client: WebClient = WebClient.create(vertx, webClientOptions)

    val uriTemplate: UriTemplate = UriTemplate.of(
      "https://api.weixin.qq" + ".com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + secret
    )
    // Send a GET request
    client.getAbs(uriTemplate).send().onSuccess { response ->
      if (response.statusCode() == 200) {
        // Decode the body as a json object
        val body: JsonObject = response.bodyAsJsonObject()
        cache.put(appId, Token(body.getString("access_token"), body.getLong("expires_in")))

      } else {
        System.out.println("Something went wrong " + response.statusCode())
      }


    }.onFailure { err -> System.out.println("Something went wrong " + err.message) }
    return cache.getIfPresent(appId)
  }

  return result;
}
