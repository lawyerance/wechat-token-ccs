package com.honing.css.spring.customized.wechat

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "wechat.api")
open class WechatOpenApiProperties {
  private var baseUri = "https://api.weixin.qq.com"

  private val miniProgram = MiniProgram()


  class MiniProgram {
    /**
     * 接口调用凭证 /获取接口调用凭据
     */
    var accessToken = "/cgi-bin/token?grant_type={grantType}&appid={appid}&secret={secret}"

    /**
     * 接口调用凭证/获取稳定版接口调用凭据
     */
    var stableAccessToken = "/cgi-bin/stable_token"
  }


  fun getBaseUri(): String {
    return baseUri
  }

  fun setBaseUri(baseUri: String) {
    this.baseUri = baseUri
  }

  fun getMiniProgram(): MiniProgram {
    return miniProgram
  }
}
