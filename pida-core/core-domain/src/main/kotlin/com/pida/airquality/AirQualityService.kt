package com.pida.airquality

/**
 * 대기질 조회 서비스
 */
interface AirQualityService {
    /**
     * 주어진 좌표의 대기질 정보 조회
     *
     * @param latitude 위도 (WGS84)
     * @param longitude 경도 (WGS84)
     * @return 대기질 정보
     */
    fun getAirQuality(
        latitude: Double,
        longitude: Double,
    ): AirQuality
}
