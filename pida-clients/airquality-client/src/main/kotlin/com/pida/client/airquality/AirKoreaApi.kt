package com.pida.client.airquality

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * 에어코리아 대기오염정보 API
 *
 * API 문서: https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15073861
 */
@FeignClient(
    name = "airkorea-api",
    url = "\${airkorea.api.base-url:http://apis.data.go.kr/B090041/openapi/service/RltmMsrstnInfoInqireSvc}",
)
internal interface AirKoreaApi {
    /**
     * 측정소별 실시간 측정정보 조회
     *
     * @param serviceKey 서비스 인증키
     * @param returnType 응답 자료 형식 (json, xml)
     * @param numOfRows 한 페이지 결과 수
     * @param pageNo 페이지 번호
     * @param stationName 측정소 이름
     * @param dataTerm 데이터 기간 (DAILY, MONTH, 3MONTH)
     * @param ver 버전
     * @return 대기질 측정 정보
     */
    @GetMapping("/getMsrstnAcctoRltmMesureDnsty")
    fun getMsrstnAcctoRltmMesureDnsty(
        @RequestParam("serviceKey") serviceKey: String,
        @RequestParam("returnType") returnType: String = "json",
        @RequestParam("numOfRows") numOfRows: Int = 1,
        @RequestParam("pageNo") pageNo: Int = 1,
        @RequestParam("stationName") stationName: String,
        @RequestParam("dataTerm") dataTerm: String = "DAILY",
        @RequestParam("ver") ver: String = "1.0",
    ): AirKoreaResponse

    /**
     * TM 기준 근접측정소 목록 조회
     *
     * @param serviceKey 서비스 인증키
     * @param returnType 응답 자료 형식
     * @param tmX TM X 좌표
     * @param tmY TM Y 좌표
     * @param ver 버전
     * @return 근접 측정소 목록
     */
    @GetMapping("/getNearbyMsrstnList")
    fun getNearbyMsrstnList(
        @RequestParam("serviceKey") serviceKey: String,
        @RequestParam("returnType") returnType: String = "json",
        @RequestParam("tmX") tmX: String,
        @RequestParam("tmY") tmY: String,
        @RequestParam("ver") ver: String = "1.0",
    ): AirKoreaStationResponse
}
