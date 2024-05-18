package com.honing.ccs.core

import com.github.benmanes.caffeine.cache.Cache
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.uritemplate.UriTemplate
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class WechatApiRestClient(private val cache: Cache<String, Token>, private val vertx: Vertx) {
  private val webClientOptions: WebClientOptions =
    WebClientOptions().setSsl(true).setTrustAll(true).setDefaultPort(443).setKeepAlive(true)

  private val logger: Logger = LoggerFactory.getLogger("com.honing.ccs.core");
  fun getToken(appid: String, secret: String, grantType: String): Future<Token> {
    val token = cache.getIfPresent("${appid}&${secret}")
    if (token != null) {
      return Future.succeededFuture(token)
    }
    val client: WebClient = WebClient.create(vertx, webClientOptions)

    val uriTemplate: UriTemplate = UriTemplate.of(
      "https://api.weixin.qq.com/cgi-bin/token?grant_type=$grantType&appid=$appid&secret=$secret"
    )
    // Send a GET request
    return client.getAbs(uriTemplate).send().onSuccess { response ->
      if (response.statusCode() == 200) {
        val bodyAsString: String = response.bodyAsString()
        logger.info("Cache is expired and do execute result: {} ", bodyAsString)
        return@onSuccess
      } else {
        logger.error("Something went wrong " + response.statusCode())
      }

    }.onFailure { err -> logger.error("Something went wrong ", err) }.map { response ->
      val result: Token = Json.decodeValue(response.bodyAsString(), Token::class.java)
      cache.put("${appid}&${secret}", result)
      result
    }
  }

  fun getStableToken(param: StableTokenRequest): Future<Token> {
    val token = cache.getIfPresent("${param.appid}&${param.secret}")
    if (token != null) {
      return Future.succeededFuture(token)
    }
    val client: WebClient = WebClient.create(vertx, webClientOptions)

    val uriTemplate: UriTemplate = UriTemplate.of(
      "https://api.weixin.qq.com/cgi-bin/stable_token"
    )
    // Send a GET request
    return client.postAbs(uriTemplate).sendJson(param).onSuccess { response ->
      if (response.statusCode() == 200) {
        // Decode the body as a json object
        logger.info("Cache is expired and do execute result: {} ", response.bodyAsString())
        return@onSuccess
      } else {
        logger.error("Something went wrong " + response.statusCode())

      }
    }.onFailure { err -> logger.error("Something went wrong ", err) }.map { response ->
      val result: Token = Json.decodeValue(
        response.bodyAsString(), Token::class.java
      )
      cache.put("${param.appid}&${param.secret}", result)
      result

    }
  }
}
