package com.honing.css.spring.service.impl

import com.honing.css.spring.autoconfigure.http.ClientHttpRequestLoggingInterceptor
import com.honing.css.spring.customized.wechat.WechatOpenApi
import com.honing.css.spring.dto.StableTokenDTO
import com.honing.css.spring.service.TokenService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.util.concurrent.TimeoutException

@Service
open class TokenServiceImpl(private val restTemplate: RestTemplate, private val wechatOpenApi: WechatOpenApi) :
  TokenService {
  private var logger: Logger = LoggerFactory.getLogger(ClientHttpRequestLoggingInterceptor::class.java)


  @Cacheable(
    cacheNames = ["public:wechat"],
    key = "#appid + '&' + #secret",
    unless = "!#result.containsKey('access_token')"
  )
  override fun getToken(grantType: String?, appid: String?, secret: String?): Map<String, Any>? {
    val uriVariables: MutableMap<String, Any?> = LinkedHashMap()
    uriVariables["grantType"] = grantType
    uriVariables["appid"] = appid
    uriVariables["secret"] = secret
    logger.info(
      "Cache is invalid, will using appid = {} to request -token url:  {}",
      appid,
      UriComponentsBuilder.fromHttpUrl(wechatOpenApi.accessToken).build(uriVariables)
    )
    return doObtainToken(uriVariables)
  }

  @Cacheable(
    cacheNames = ["public:wechat"],
    key = "#param.appid + '&' + #param.secret",
    unless = "!#result.containsKey('access_token')"
  )
  override fun getStableToken(param: StableTokenDTO?): Map<String, Any>? {
    logger.info(
      "Cache is invalid, will using appid = {} to request stable-token url:  {}",
      param?.appid,
      wechatOpenApi.stableAccessToken
    )
    return doObtainStableToken(param)
  }


  @Retryable(
    maxAttempts = 5, label = "Retry token restful.", retryFor = [TimeoutException::class, InterruptedException::class]
  )
  fun doObtainToken(uriVariables: Map<String, Any?>?): Map<String, Any>? {
    val accessToken: String = wechatOpenApi.accessToken
    val exchange: ResponseEntity<Map<String, Any>> = restTemplate.exchange<Map<String, Any>>(
      accessToken, HttpMethod.GET, null, object : ParameterizedTypeReference<Map<String, Any>?>() {}, uriVariables!!
    )
    return exchange.body
  }

  @Retryable(
    maxAttempts = 5, label = "Retry token restful.", retryFor = [TimeoutException::class, InterruptedException::class]
  )
  fun doObtainStableToken(param: StableTokenDTO?): Map<String, Any>? {
    val stableAccessToken: String = wechatOpenApi.stableAccessToken
    val headers = HttpHeaders()
    headers.contentType = MediaType.APPLICATION_JSON
    val exchange: ResponseEntity<Map<String, Any>> = restTemplate.exchange<Map<String, Any>>(stableAccessToken,
      HttpMethod.POST,
      HttpEntity(param, headers),
      object : ParameterizedTypeReference<Map<String, Any>?>() {})
    return exchange.body
  }
}
