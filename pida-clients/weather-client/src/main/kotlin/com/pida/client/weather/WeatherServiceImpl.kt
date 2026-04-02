package com.pida.client.weather

import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import com.pida.support.extension.logger
import com.pida.support.resilience.ExternalDependency
import com.pida.support.resilience.ExternalDependencyPolicy
import com.pida.weather.PrecipitationType
import com.pida.weather.SkyCondition
import com.pida.weather.Weather
import com.pida.weather.WeatherLocation
import com.pida.weather.WeatherService
import feign.FeignException
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

/**
 * 날씨 서비스 구현체
 */
@Service
class WeatherServiceImpl(
    private val kmaForecastClient: KmaForecastClient,
    private val kmaForecastCache: KmaForecastCache,
    private val externalDependencyPolicy: ExternalDependencyPolicy,
) : WeatherService {
    private val logger by logger()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    override fun getWeather(location: WeatherLocation): Weather {
        val items = fetchForecastItems(location)
        return parseWeatherResponse(items, location)
    }

    override fun getTomorrowMaxPrecipitationProbability(location: WeatherLocation): Int {
        val items = fetchForecastItems(location)

        if (items.isEmpty()) {
            logger.warn("KMA response contains no forecast items for nx=${location.nx}, ny=${location.ny}")
            throw ErrorException(ErrorType.WEATHER_DATA_NOT_AVAILABLE)
        }

        val tomorrowDate = LocalDate.now().plusDays(1).format(dateFormatter)

        return items
            .asSequence()
            .filter { it.category == "POP" && it.fcstDate == tomorrowDate }
            .mapNotNull { it.fcstValue.toIntOrNull() }
            .maxOrNull() ?: 0
    }

    /**
     * 기상청 API 응답을 Weather 도메인 모델로 변환
     */
    private fun parseWeatherResponse(
        items: List<KmaWeatherResponse.Item>,
        location: WeatherLocation,
    ): Weather {
        if (items.isEmpty()) {
            logger.warn("KMA response contains no forecast items for nx=${location.nx}, ny=${location.ny}")
            throw ErrorException(ErrorType.WEATHER_DATA_NOT_AVAILABLE)
        }

        // 현재 시각과 가장 가까운 예보 시각의 데이터 추출
        val now = LocalDateTime.now()
        val nearestForecast =
            items
                .groupBy { "${it.fcstDate}${it.fcstTime}" }
                .minByOrNull { (dateTime, _) ->
                    val forecastDateTime = parseForecastDateTime(dateTime)
                    abs(
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

    private fun fetchForecastItems(location: WeatherLocation): List<KmaWeatherResponse.Item> {
        val (baseDate, baseTime) = kmaForecastClient.getLatestBaseTime()
        return try {
            kmaForecastCache.getOrLoad(
                baseDate = baseDate,
                baseTime = baseTime,
                nx = location.nx,
                ny = location.ny,
            ) {
                logger.info(
                    "Fetching weather for location: lat=${location.latitude}, lon=${location.longitude}, nx=${location.nx}, ny=${location.ny}",
                )
                kmaForecastClient
                    .getVilageForecast(baseDate, baseTime, location.nx, location.ny)
                    .response.body.items.item
            }
        } catch (error: FeignException.TooManyRequests) {
            logger.warn(
                "KMA forecast API rate limit exceeded for nx=${location.nx}, ny=${location.ny}, baseDate=$baseDate, baseTime=$baseTime",
                error,
            )
            staleForecastOrNull(location.nx, location.ny)?.let { staleItems ->
                externalDependencyPolicy.recordFallback(
                    dependency = ExternalDependency.KMA_WEATHER,
                    reason = "stale-forecast-rate-limit",
                    throwable = error,
                )
                logger.warn(
                    "Using stale KMA forecast cache due to rate limit for nx=${location.nx}, ny=${location.ny}",
                )
                return staleItems
            }
            throw ErrorException(ErrorType.EXCEED_RATE_LIMIT)
        } catch (error: Exception) {
            staleForecastOrNull(location.nx, location.ny)?.let { staleItems ->
                externalDependencyPolicy.recordFallback(
                    dependency = ExternalDependency.KMA_WEATHER,
                    reason = "stale-forecast",
                    throwable = error,
                )
                logger.warn(
                    "Using stale KMA forecast cache due to upstream failure for nx=${location.nx}, ny=${location.ny}",
                )
                return staleItems
            }
            logger.error("Failed to fetch weather forecast", error)
            throw ErrorException(ErrorType.WEATHER_API_CALL_FAILED)
        }
    }

    private fun staleForecastOrNull(
        nx: Int,
        ny: Int,
    ): List<KmaWeatherResponse.Item>? = kmaForecastCache.getLatestByGrid(nx, ny)

    /**
     * 예보 일시 파싱 (yyyyMMddHHmm -> LocalDateTime)
     */
    private fun parseForecastDateTime(dateTime: String): LocalDateTime =
        LocalDateTime.parse(
            dateTime,
            DateTimeFormatter
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
