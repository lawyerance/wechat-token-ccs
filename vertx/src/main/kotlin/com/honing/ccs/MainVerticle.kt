package com.honing.ccs

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.honing.ccs.core.Token
import com.honing.ccs.core.WechatExecutor
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import java.util.*
import java.util.concurrent.TimeUnit


class MainVerticle : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {

    val cache: Cache<String, Token> = Caffeine.newBuilder().expireAfterWrite(7000L, TimeUnit.SECONDS).build();
    val server: HttpServer = vertx.createHttpServer();

    val router: Router = Router.router(vertx)

    router.get("/cgi-bin/token").respond { ctx ->
      val we: WechatExecutor = WechatExecutor(
        Collections.singletonMap(
          "1", "2"
        ), cache
      );
      println(we.getToken("wx752f87b6340e0e52"))
      ctx.response().putHeader("Content-Type", "application/json").end("123")
    }
    server.requestHandler(router).listen(8080) { http ->
      if (http.succeeded()) {
        startPromise.complete()
        println("HTTP server started on port 8888")
      } else {
        startPromise.fail(http.cause());
      }
    }
  }
}
