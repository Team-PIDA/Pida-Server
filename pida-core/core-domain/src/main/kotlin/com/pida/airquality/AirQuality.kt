package com.pida.airquality

import java.time.LocalDateTime

/**
 * 대기질 정보
 */
data class AirQuality(
    val pm10: Int, // 미세먼지 농도 (µg/m³)
    val pm25: Int, // 초미세먼지 농도 (µg/m³)
    val measurementTime: LocalDateTime, // 측정 시각
    val stationName: String, // 측정소 이름
) {
    /**
     * 미세먼지가 '나쁨' 이상인지 확인
     * PM10 기준: 81µg/m³ 이상
     */
    fun isBadPm10(): Boolean = pm10 >= PM10_BAD_THRESHOLD

    /**
     * 초미세먼지가 '나쁨' 이상인지 확인
     * PM2.5 기준: 36µg/m³ 이상
     */
    fun isBadPm25(): Boolean = pm25 >= PM25_BAD_THRESHOLD

    /**
     * 대기질이 좋은지 확인 (미세먼지와 초미세먼지 모두 나쁨 미만)
     */
    fun isGood(): Boolean = !isBadPm10() && !isBadPm25()

    companion object {
        const val PM10_BAD_THRESHOLD = 81 // µg/m³
        const val PM25_BAD_THRESHOLD = 36 // µg/m³
    }
}
