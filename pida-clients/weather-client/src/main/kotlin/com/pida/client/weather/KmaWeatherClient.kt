package com.pida.client.weather

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 기상청 단기예보 API 클라이언트
 *
 * API 문서: https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15084084
 */
@Component
class KmaWeatherClient internal constructor(
    // 단일 변수이니 properties 대신 value로 선언
    @param:Value("\${kma.api.service-key:}")
    private val serviceKey: String,
    private val kmaWeatherApi: KmaWeatherApi,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 단기예보 조회
     *
     * @param baseDate 발표일자 (yyyyMMdd)
     * @param baseTime 발표시각 (HHmm) - 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300
     * @param nx 예보지점 X 좌표
     * @param ny 예보지점 Y 좌표
     * @param numOfRows 한 페이지 결과 수 (기본값: 1000)
     * @return 기상청 단기예보 응답
     */
    fun getVilageForecast(
        baseDate: String,
        baseTime: String,
        nx: Int,
        ny: Int,
        numOfRows: Int = 1000,
    ): KmaWeatherResponse {
        logger.info("Fetching Vilage Forecast: baseDate=$baseDate, baseTime=$baseTime, nx=$nx, ny=$ny")
        return kmaWeatherApi.getVilageForecast(
            serviceKey = serviceKey,
            numOfRows = numOfRows,
            baseDate = baseDate,
            baseTime = baseTime,
            nx = nx,
            ny = ny,
        )
    }

    /**
     * 현재 시각 기준 최신 발표 시각 조회
     * 기상청 단기예보는 하루 8번 발표 (02:00, 05:00, 08:00, 11:00, 14:00, 17:00, 20:00, 23:00)
     * API 제공 시간은 발표시각 + 10분
     */
    fun getLatestBaseTime(currentTime: LocalDateTime = LocalDateTime.now()): Pair<String, String> {
        val baseTimes = listOf("0200", "0500", "0800", "1100", "1400", "1700", "2000", "2300")
        val currentHourMinute = currentTime.format(DateTimeFormatter.ofPattern("HHmm")).toInt()

        // API 제공 시간을 고려하여 10분 빼기
        val adjustedTime = currentTime.minusMinutes(10)
        val adjustedHourMinute = adjustedTime.format(DateTimeFormatter.ofPattern("HHmm")).toInt()

        // 가장 최근 발표 시각 찾기
        val baseTime =
            baseTimes
                .map { it.toInt() }
                .filter { it <= adjustedHourMinute }
                .maxOrNull()
                ?.toString()
                ?.padStart(4, '0')
                ?: baseTimes.last() // 오늘 발표 전이면 어제 마지막 발표 시각

        val baseDate =
            if (baseTime == baseTimes.last() && adjustedHourMinute < baseTimes.first().toInt()) {
                adjustedTime.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            } else {
                adjustedTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            }

        return Pair(baseDate, baseTime)
    }
}
