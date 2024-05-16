package com.honing.css.spring.autoconfigure.http

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.util.StreamUtils
import java.io.IOException
import java.nio.charset.Charset

class ClientHttpRequestLoggingInterceptor : ClientHttpRequestInterceptor {
  private var logger: Logger = LoggerFactory.getLogger(ClientHttpRequestLoggingInterceptor::class.java)

  @Throws(IOException::class)
  override fun intercept(
    request: HttpRequest,
    body: ByteArray,
    execution: ClientHttpRequestExecution
  ): ClientHttpResponse {
    logRequest(request, body)
    val response = execution.execute(request, body)
    logResponse(response)
    return response
  }

  @Throws(IOException::class)
  private fun logRequest(request: HttpRequest, body: ByteArray) {
    if (logger.isDebugEnabled()) {
      logger.debug("--> {} {}", request.method, request.uri)
      request.headers.forEach { k: String?, v: List<String?>? ->
        logger.debug(
          "--> {}: {}",
          k,
          java.lang.String.join(";", v)
        )
      }
      logger.debug("--> {}", String(body, charset("UTF-8")))
    }
  }

  @Throws(IOException::class)
  private fun logResponse(response: ClientHttpResponse) {
    if (logger.isDebugEnabled()) {
      logger.debug("<-- {} {}", response.statusCode, response.statusText)
      response.headers.forEach { k: String?, v: List<String?>? ->
        logger.debug(
          "<-- {}: {}",
          k,
          java.lang.String.join(";", v)
        )
      }
      logger.debug("<-- {}", StreamUtils.copyToString(response.body, Charset.defaultCharset()))
    }
  }
}
