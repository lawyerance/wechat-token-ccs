package com.honing.css.spring.autoconfigure.http

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.converter.xml.SourceHttpMessageConverter
import org.springframework.web.client.RestTemplate
import javax.xml.transform.Source

@Configuration
open class RestTemplateConfiguration {
  @Bean
  open fun restTemplate(restTemplateBuilder: RestTemplateBuilder): RestTemplate {
    restTemplateBuilder.additionalMessageConverters(SourceHttpMessageConverter<Source>())
    restTemplateBuilder.additionalInterceptors(setOf(ClientHttpRequestLoggingInterceptor()))
    val restTemplate = restTemplateBuilder.build()
    // 解决spring6.x针对json类型入参，不再设置Content-Length问题
    restTemplate.setRequestFactory(BufferingClientHttpRequestFactory(restTemplate.getRequestFactory()))
    return restTemplate
  }
}
