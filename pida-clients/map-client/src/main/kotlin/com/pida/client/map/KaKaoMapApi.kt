package com.pida.client.map

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(value = "kakao-map-api", url = "https://dapi.kakao.com")
internal interface KaKaoMapApi {
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/v2/local/search/keyword"],
        consumes = ["application/x-www-form-urlencoded;charset=utf-8"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun searchKeyword(
        @RequestHeader(name = "Authorization") authorization: String,
    ): KakaoSearchResponse
}
