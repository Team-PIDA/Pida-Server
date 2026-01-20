package com.pida.client.weather

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * 기상청 단기예보 API
 *
 * API 문서: https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15084084
 */
@FeignClient(
    name = "kma-weather-api",
    url = "\${kma.api.base-url:http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0}",
)
internal interface KmaWeatherApi {
    /**
     * 단기예보 조회
     *
     * @param serviceKey 서비스 인증키 (디코딩된 원본 값)
     * @param pageNo 페이지 번호
     * @param numOfRows 한 페이지 결과 수
     * @param dataType 응답 자료 형식 (JSON)
     * @param baseDate 발표일자 (yyyyMMdd)
     * @param baseTime 발표시각 (HHmm) - 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300
     * @param nx 예보지점 X 좌표
     * @param ny 예보지점 Y 좌표
     * @return 기상청 단기예보 응답
     */
    @GetMapping("/getVilageFcst")
    fun getVilageForecast(
        @RequestParam("serviceKey") serviceKey: String,
        @RequestParam("pageNo") pageNo: Int = 1,
        @RequestParam("numOfRows") numOfRows: Int,
        @RequestParam("dataType") dataType: String = "JSON",
        @RequestParam("base_date") baseDate: String,
        @RequestParam("base_time") baseTime: String,
        @RequestParam("nx") nx: Int,
        @RequestParam("ny") ny: Int,
    ): KmaWeatherResponse
}
