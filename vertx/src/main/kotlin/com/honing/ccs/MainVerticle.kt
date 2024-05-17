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
import java.util.concurrent.TimeUnit


class MainVerticle : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {

    val cache: Cache<String, Token> = Caffeine.newBuilder().expireAfterWrite(7000L, TimeUnit.SECONDS).build();
    val server: HttpServer = vertx.createHttpServer();
    val webClient: WechatApiRestClient = WechatApiRestClient(cache, vertx);
    val router: Router = Router.router(vertx)

    router.get("/cgi-bin/token").respond { ctx ->
      val httpServerRequest: HttpServerRequest = ctx.request();
      val grantType: String = httpServerRequest.getParam("grant_type", "client_credential")
      val appid: String = httpServerRequest.getParam("appid")
      val secret: String = httpServerRequest.getParam("secret")
      val token = webClient.getToken(appid, secret, grantType);
      ctx.response().putHeader("Content-Type", "application/json").end(Json.encodePrettily(token))
    }

    router.get("/cgi-bin/stable-token").respond { ctx ->
      val decodeValue: StableTokenRequest = Json.decodeValue(ctx.body().asString(), StableTokenRequest::class.java)
      val token = webClient.getStableToken(decodeValue)
      ctx.response().putHeader("Content-Type", "application/json").end(Json.encodePrettily(token))
    }
    server.requestHandler(router).listen(18080) { http ->
      if (http.succeeded()) {
        startPromise.complete()
        println("HTTP server started on port 8888")
      } else {
        startPromise.fail(http.cause());
      }
    }
  }
}
