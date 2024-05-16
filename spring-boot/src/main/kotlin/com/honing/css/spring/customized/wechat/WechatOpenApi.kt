package com.honing.css.spring.customized.wechat

data class WechatOpenApi(
  /**
   * 接口调用凭证 /获取接口调用凭据
   */
  val accessToken: String

  /**
   * 接口调用凭证/获取稳定版接口调用凭据
   */
  , val stableAccessToken: String
)
