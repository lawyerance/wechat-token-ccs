package com.honing.css.spring.controller

import com.honing.css.spring.dto.StableTokenDTO
import com.honing.css.spring.service.TokenService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/cgi-bin"])
class CgiTokenController(private val tokenService: TokenService) {


  @RequestMapping(value = ["/token"], method = [RequestMethod.GET])
  fun getToken(
    @RequestParam(value = "grant_type", defaultValue = "client_credential", required = false) grantType: String?,
    @RequestParam(value = "appid") appid: String?,
    @RequestParam(value = "secret") secret: String?
  ): ResponseEntity<Map<String, Any>> {
    val token: Map<String, Any>? = tokenService.getToken(grantType, appid, secret)
    return ResponseEntity.ok(token)
  }

  @RequestMapping(value = ["/stable_token"], method = [RequestMethod.POST])
  fun getStableToken(@RequestBody param: StableTokenDTO?): ResponseEntity<Map<String, Any>> {
    val token: Map<String, Any>? = tokenService.getStableToken(param)
    return ResponseEntity.ok(token)
  }
}
