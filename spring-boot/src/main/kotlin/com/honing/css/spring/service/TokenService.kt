package com.honing.css.spring.service

import com.honing.css.spring.dto.StableTokenDTO
import org.springframework.lang.Nullable

interface TokenService {
  fun getToken(@Nullable grantType: String?, appid: String?, secret: String?): Map<String, Any>?

  fun getStableToken(param: StableTokenDTO?): Map<String, Any>?
}
