package com.pida.scheduler

import com.pida.support.extension.logger
import com.pida.weather.WeatherLocation
import com.pida.weather.WeatherService
import org.springframework.stereotype.Component

/**
 * 날씨 API 테스트를 위한 스케줄러
 * 10분마다 서울 중구의 날씨 데이터를 조회하여 로그로 출력합니다.
 */
@Component
class WeatherScheduler(
    private val weatherService: WeatherService,
) {
    private val logger by logger()

    // TODO: 테스트 용이니 해당 메서스는 추가 디벨롭으로 푸시 알림 개발
//    @Scheduled(fixedRate = 60000, initialDelay = 10000)

    /**
     * 10분(600,000ms)마다 날씨 데이터 조회 및 로깅
     * 서울 중구 기준 (위도: 37.5636, 경도: 126.9970, 격자 X: 60, 격자 Y: 127)
     */
    fun fetchAndLogWeather() {
        try {
            logger.info("=".repeat(80))
            logger.info("날씨 데이터 조회 시작...")

            // 서울 중구 좌표
            val seoulLocation =
                WeatherLocation(
                    latitude = 37.5636,
                    longitude = 126.9970,
                    nx = 60,
                    ny = 127,
                )

            val weather = weatherService.getWeather(seoulLocation)

            logger.info("서울 중구 날씨 데이터 조회 성공")
            logger.info("위치: 위도=${seoulLocation.latitude}, 경도=${seoulLocation.longitude}")
            logger.info("격자 좌표: X=${seoulLocation.nx}, Y=${seoulLocation.ny}")
            logger.info("예보 시각: ${weather.forecastDateTime}")
            logger.info("기온: ${weather.temperature}°C")
            logger.info("습도: ${weather.humidity}%")
            logger.info("하늘 상태: ${weather.skyCondition}")
            logger.info("강수 형태: ${weather.precipitationType}")
            logger.info("강수 확률: ${weather.precipitationProbability}%")
            logger.info("강수량: ${weather.precipitation}mm")
            logger.info("=".repeat(80))
        } catch (e: Exception) {
            logger.error("날씨 데이터 조회 실패", e)
            logger.error("에러 메시지: ${e.message}")
            logger.info("=".repeat(80))
        }
    }
}
