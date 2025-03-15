package com.pida.authentication.client.oauth

import com.pida.authentication.client.oauth.response.KaKaoUserResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(value = "kakao-auth-api", url = "https://kapi.kakao.com")
internal interface KaKaoApi {
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/v2/user/me"],
        consumes = ["application/x-www-form-urlencoded;charset=utf-8"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun getKaKaoUserInfo(
        @RequestHeader(name = "Authorization") authorization: String,
    ): KaKaoUserResponse
}
