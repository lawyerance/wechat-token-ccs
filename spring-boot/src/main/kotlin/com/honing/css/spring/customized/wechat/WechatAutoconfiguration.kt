package com.honing.css.spring.customized.wechat

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(WechatOpenApiProperties::class)
@Configuration
open class WechatAutoconfiguration {
  @Bean
  open fun wechatOpenApi(wechatOpenApiProperties: WechatOpenApiProperties): WechatOpenApi {
    val baseUri = wechatOpenApiProperties.getBaseUri()
    val program = wechatOpenApiProperties.getMiniProgram()
    return WechatOpenApi(baseUri + program.accessToken, baseUri + program.stableAccessToken)
  }
}
