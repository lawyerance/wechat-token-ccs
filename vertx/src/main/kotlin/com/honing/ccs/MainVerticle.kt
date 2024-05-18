package com.honing.ccs

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.honing.ccs.core.StableTokenRequest
import com.honing.ccs.core.Token
import com.honing.ccs.core.WechatApiRestClient
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.LoggerHandler
import java.util.concurrent.TimeUnit


class MainVerticle : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    val cache: Cache<String, Token> = Caffeine.newBuilder().expireAfterWrite(7000L, TimeUnit.SECONDS).build()
    val server: HttpServer = vertx.createHttpServer();
    val webClient: WechatApiRestClient = WechatApiRestClient(cache, vertx);
    val router: Router = Router.router(vertx)

    router.get("/cgi-bin/token").handler(LoggerHandler.create()).respond { ctx ->
      val httpServerRequest: HttpServerRequest = ctx.request();
      val grantType: String = httpServerRequest.getParam("grant_type", "client_credential")
      val appid: String = httpServerRequest.getParam("appid")
      val secret: String = httpServerRequest.getParam("secret")
      ctx.response().putHeader("Content-Type", "application/json")
      webClient.getToken(appid, secret, grantType)

//      ctx.response().putHeader("Content-Type", "application/json").end(Json.encodePrettily(token))
    }

    router.post("/cgi-bin/stable_token").handler(BodyHandler.create()).respond { ctx ->
      val decodeValue: StableTokenRequest = Json.decodeValue(ctx.body().asString(), StableTokenRequest::class.java)
      ctx.response().putHeader("Content-Type", "application/json")
      webClient.getStableToken(decodeValue)
    }
    server.requestHandler(router).listen(18180) { http ->
      if (http.succeeded()) {
        startPromise.complete()
        println("HTTP server started on port 18180")
      } else {
        startPromise.fail(http.cause());
      }
    }
  }
}
