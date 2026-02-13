package com.pida.notification.weekend

import com.pida.airquality.AirQuality
import com.pida.airquality.AirQualityService
import com.pida.support.extension.logger
import org.springframework.stereotype.Component

/**
 * 주말 알림 대기질 기반 필터링 컴포넌트
 *
 * 미세먼지 농도가 나쁨(81µg/m³) 미만인지 확인
 */
@Component
class WeekendNotificationAirQualityChecker(
    private val airQualityService: AirQualityService,
) {
    private val logger by logger()

    /**
     * 대기질이 좋은지 확인 (미세먼지 나쁨 미만)
     *
     * @param latitude 위도
     * @param longitude 경도
     * @return PM10이 나쁨 미만이면 true, 에러 발생 시 true (알림 차단 방지)
     */
    fun hasGoodAirQuality(
        latitude: Double,
        longitude: Double,
    ): Boolean =
        try {
            val airQuality = airQualityService.getAirQuality(latitude, longitude)

            val isGood = airQuality.pm10 < AirQuality.PM10_BAD_THRESHOLD

            if (!isGood) {
                logger.debug(
                    "PM10 ${airQuality.pm10} >= ${AirQuality.PM10_BAD_THRESHOLD} at ($latitude, $longitude), " +
                        "station: ${airQuality.stationName}",
                )
            } else {
                logger.debug(
                    "Good air quality (PM10: ${airQuality.pm10}) at ($latitude, $longitude), " +
                        "station: ${airQuality.stationName}",
                )
            }

            isGood
        } catch (e: Exception) {
            logger.warn("Failed to fetch air quality for ($latitude, $longitude), allowing notification", e)
            // 대기질 조회 실패 시 알림을 차단하지 않음
            true
        }
}
