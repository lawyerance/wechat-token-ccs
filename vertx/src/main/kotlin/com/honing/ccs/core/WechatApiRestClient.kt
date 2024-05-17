package com.honing.ccs.core

import com.github.benmanes.caffeine.cache.Cache
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.uritemplate.UriTemplate
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class WechatApiRestClient(private val cache: Cache<String, Token>, private val vertx: Vertx) {
  private val webClientOptions: WebClientOptions =
    WebClientOptions().setSsl(true).setTrustAll(true).setDefaultPort(443).setKeepAlive(true);

  private val logger: Logger = LoggerFactory.getLogger("com.honing.ccs.core");
  fun getToken(appId: String, secret: String, grantType: String): Token {
    val token = cache.getIfPresent("$appId&$secret")
    if (token != null) {
      return token;
    }
    val client: WebClient = WebClient.create(Vertx.vertx(), webClientOptions)

    val uriTemplate: UriTemplate = UriTemplate.of(
      "https://api.weixin.qq.com/cgi-bin/token?grant_type=$grantType&appid=$appId&secret=$secret"
    )
    // Send a GET request
    client.getAbs(uriTemplate).send().onSuccess { response ->
      if (response.statusCode() == 200) {

        // Decode the body as a json object
        val body: JsonObject = response.bodyAsJsonObject()
        logger.error("Cache is expired and do execute result: {} ", body)
        cache.put(appId, Token(body.getString("access_token"), body.getLong("expires_in")))
      } else {
        logger.error("Something went wrong " + response.statusCode())
      }
    }.onFailure { err -> logger.error("Something went wrong ", err) }
    return cache.getIfPresent(appId)!!
  }

  fun getStableToken(param: StableTokenRequest): Token {
    val token = cache.getIfPresent("$param.appId&$param.secret")
    if (token != null) {
      return token;
    }
    val client: WebClient = WebClient.create(Vertx.vertx(), webClientOptions)

    val uriTemplate: UriTemplate = UriTemplate.of(
      "https://api.weixin.qq.com/cgi-bin/stable_token"
    )
    // Send a GET request
    client.getAbs(uriTemplate).send().onSuccess { response ->
      if (response.statusCode() == 200) {

        // Decode the body as a json object
        val body: JsonObject = response.bodyAsJsonObject()
        logger.error("Cache is expired and do execute result: {} ", body)
        cache.put("$param.appId&$param.secret", Token(body.getString("access_token"), body.getLong("expires_in")))
      } else {
        logger.error("Something went wrong " + response.statusCode())
      }
    }.onFailure { err -> logger.error("Something went wrong ", err) }
    return cache.getIfPresent("$param.appId&$param.secret")!!
  }
}
