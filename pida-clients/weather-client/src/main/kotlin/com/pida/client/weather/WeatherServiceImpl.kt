package com.pida.client.weather

import com.pida.weather.PrecipitationType
import com.pida.weather.SkyCondition
import com.pida.weather.Weather
import com.pida.weather.WeatherLocation
import com.pida.weather.WeatherService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * 날씨 서비스 구현체
 */
@Service
class WeatherServiceImpl(
    private val kmaWeatherClient: KmaWeatherClient,
) : com.pida.weather.WeatherService {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun getWeather(location: WeatherLocation): Weather {
        val (baseDate, baseTime) = kmaWeatherClient.getLatestBaseTime()

        logger.info(
            "Fetching weather for location: lat=${location.latitude}, lon=${location.longitude}, nx=${location.nx}, ny=${location.ny}",
        )

        val response =
            kmaWeatherClient
                .getVilageForecast(baseDate, baseTime, location.nx, location.ny)
                .doOnError { error ->
                    logger.error("Failed to fetch weather forecast", error)
                }.block() ?: throw RuntimeException("Failed to fetch weather data")

        return parseWeatherResponse(response, location)
    }

    /**
     * 기상청 API 응답을 Weather 도메인 모델로 변환
     */
    private fun parseWeatherResponse(
        response: KmaWeatherResponse,
        location: WeatherLocation,
    ): Weather {
        val items = response.response.body.items.item

        // 현재 시각과 가장 가까운 예보 시각의 데이터 추출
        val now = LocalDateTime.now()
        val nearestForecast =
            items
                .groupBy { "${it.fcstDate}${it.fcstTime}" }
                .minByOrNull { (dateTime, _) ->
                    val forecastDateTime = parseForecastDateTime(dateTime)
                    kotlin.math.abs(
                        java.time.Duration
                            .between(now, forecastDateTime)
                            .toMinutes(),
                    )
                }?.value ?: emptyList()

        // 카테고리별 데이터 추출
        val forecastMap = nearestForecast.associateBy { it.category }

        val precipitationType =
            forecastMap["PTY"]?.fcstValue?.let {
                PrecipitationType.fromCode(it)
            } ?: PrecipitationType.NONE

        val precipitationProbability = forecastMap["POP"]?.fcstValue?.toIntOrNull() ?: 0
        val precipitation = forecastMap["PCP"]?.fcstValue?.let { parsePrecipitation(it) } ?: 0.0
        val skyCondition =
            forecastMap["SKY"]?.fcstValue?.let {
                SkyCondition.fromCode(it)
            } ?: SkyCondition.CLEAR
        val temperature = forecastMap["TMP"]?.fcstValue?.toDoubleOrNull() ?: 0.0
        val humidity = forecastMap["REH"]?.fcstValue?.toIntOrNull() ?: 0

        val forecastDateTime =
            nearestForecast.firstOrNull()?.let {
                parseForecastDateTime("${it.fcstDate}${it.fcstTime}")
            } ?: now

        return Weather(
            location = location,
            precipitationType = precipitationType,
            precipitationProbability = precipitationProbability,
            precipitation = precipitation,
            skyCondition = skyCondition,
            temperature = temperature,
            humidity = humidity,
            forecastDateTime = forecastDateTime,
        )
    }

    /**
     * 예보 일시 파싱 (yyyyMMddHHmm -> LocalDateTime)
     */
    private fun parseForecastDateTime(dateTime: String): LocalDateTime =
        LocalDateTime.parse(
            dateTime,
            java.time.format.DateTimeFormatter
                .ofPattern("yyyyMMddHHmm"),
        )

    /**
     * 강수량 파싱
     * "1mm 미만", "30~50mm" 등의 문자열을 숫자로 변환
     */
    private fun parsePrecipitation(value: String): Double =
        when {
            value == "강수없음" -> 0.0
            value.contains("미만") -> 0.5
            value.contains("~") -> {
                val range = value.replace("mm", "").split("~")
                if (range.size == 2) {
                    (range[0].toDoubleOrNull()?.plus(range[1].toDoubleOrNull() ?: 0.0))?.div(2.0) ?: 0.0
                } else {
                    0.0
                }
            }
            value.contains("이상") -> {
                value
                    .replace("mm", "")
                    .replace("이상", "")
                    .trim()
                    .toDoubleOrNull() ?: 0.0
            }
            else -> {
                value.replace("mm", "").trim().toDoubleOrNull() ?: 0.0
            }
        }
}
