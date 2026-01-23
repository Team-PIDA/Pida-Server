package com.pida.client.map

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(value = "kakao-map-api", url = "https://dapi.kakao.com")
internal interface KaKaoMapApi {
    /**
     * 카카오 로컬 키워드 검색 API
     *
     * @param authorization 카카오 REST API 키 (형식: "KakaoAK {REST_API_KEY}")
     * @param query 검색을 원하는 질의어
     * @param categoryGroupCode 카테고리 그룹 코드 (예: "SW8" - 지하철역, "AT4" - 관광명소)
     * @param x 중심 좌표의 X 혹은 경도(longitude) 값
     * @param y 중심 좌표의 Y 혹은 위도(latitude) 값
     * @param radius 중심 좌표부터의 반경거리 (단위: m, 최대 20000)
     * @return 키워드 검색 결과
     */
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/v2/local/search/keyword"],
        consumes = ["application/x-www-form-urlencoded;charset=utf-8"],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun searchKeyword(
        @RequestHeader(name = "Authorization") authorization: String,
        @RequestParam("query") query: String,
        @RequestParam("category_group_code", required = false) categoryGroupCode: String?,
        @RequestParam("x", required = false) x: String,
        @RequestParam("y", required = false) y: String,
        @RequestParam("radius", required = false) radius: Int,
    ): KakaoSearchResponse
}
