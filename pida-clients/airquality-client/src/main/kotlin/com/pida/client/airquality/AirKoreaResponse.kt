package com.pida.client.airquality

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * 에어코리아 실시간 대기질 정보 응답
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class AirKoreaResponse(
    val response: Response,
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Response(
        val header: Header,
        val body: Body,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Header(
        val resultCode: String,
        val resultMsg: String,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Body(
        val items: List<Item>?,
        val numOfRows: Int,
        val pageNo: Int,
        val totalCount: Int,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Item(
        val stationName: String, // 측정소 이름
        val dataTime: String, // 측정 일시 (yyyy-MM-dd HH:mm)
        val pm10Value: String?, // PM10 농도 (µg/m³)
        val pm25Value: String?, // PM2.5 농도 (µg/m³)
        val khaiValue: String?, // 통합대기환경지수
        val so2Value: String?, // 아황산가스 농도
        val coValue: String?, // 일산화탄소 농도
        val o3Value: String?, // 오존 농도
        val no2Value: String?, // 이산화질소 농도
    )
}

/**
 * 에어코리아 근접 측정소 응답
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class AirKoreaStationResponse(
    val response: Response,
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Response(
        val header: Header,
        val body: Body,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Header(
        val resultCode: String,
        val resultMsg: String,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Body(
        val items: List<StationItem>?,
        val numOfRows: Int?,
        val pageNo: Int?,
        val totalCount: Int?,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class StationItem(
        val stationName: String, // 측정소 이름
        val addr: String, // 측정소 주소
        val tm: Double, // TM 거리
    )
}
