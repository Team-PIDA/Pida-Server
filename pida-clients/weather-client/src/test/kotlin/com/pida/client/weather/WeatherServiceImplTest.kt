package com.pida.client.weather

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pida.support.cache.Cache
import com.pida.support.cache.CacheAdvice
import com.pida.support.cache.CacheRepository
import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import com.pida.support.resilience.ExternalDependencyPolicy
import com.pida.weather.WeatherLocation
import feign.FeignException
import feign.Request
import feign.Response
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeatherServiceImplTest {
    private val kmaForecastClient = FakeKmaForecastClient()
    private val cacheRepository = FakeCacheRepository()
    private val objectMapper = jacksonObjectMapper()
    private val cache = Cache(CacheAdvice(cacheRepository, objectMapper))
    private val kmaForecastCache = KmaForecastCache(cache, cacheRepository, objectMapper)
    private val externalDependencyPolicy = mockk<ExternalDependencyPolicy>()
    private val weatherService = WeatherServiceImpl(kmaForecastClient, kmaForecastCache, externalDependencyPolicy)

    init {
        every { externalDependencyPolicy.recordFallback(any(), any(), any()) } just runs
    }

    @Test
    fun `캐시 loader 결과로 내일 최대 강수확률을 계산한다`() {
        val location = weatherLocation()
        val response = weatherResponse(baseDate = "20260321", baseTime = "1700", probabilities = listOf(20, 80))

        kmaForecastClient.enqueueBaseTime("20260321" to "1700")
        kmaForecastClient.enqueueResponse("20260321", "1700", 59, 128, response)

        weatherService.getTomorrowMaxPrecipitationProbability(location) shouldBe 80

        kmaForecastClient.requestCount("20260321", "1700", 59, 128) shouldBe 1
    }

    @Test
    fun `429가 발생하면 최근 성공 캐시로 degrade 한다`() {
        val location = weatherLocation()
        val cachedResponse = weatherResponse(baseDate = "20260321", baseTime = "1400", probabilities = listOf(70))

        kmaForecastClient.enqueueBaseTime("20260321" to "1400")
        kmaForecastClient.enqueueBaseTime("20260321" to "1700")
        kmaForecastClient.enqueueResponse("20260321", "1400", 59, 128, cachedResponse)
        kmaForecastClient.enqueueError("20260321", "1700", 59, 128, tooManyRequests())

        weatherService.getTomorrowMaxPrecipitationProbability(location) shouldBe 70
        weatherService.getTomorrowMaxPrecipitationProbability(location) shouldBe 70

        kmaForecastClient.requestCount("20260321", "1400", 59, 128) shouldBe 1
        kmaForecastClient.requestCount("20260321", "1700", 59, 128) shouldBe 1
    }

    @Test
    fun `429이고 최근 성공 캐시도 없으면 초과 요청 예외를 반환한다`() {
        val location = weatherLocation()

        kmaForecastClient.enqueueBaseTime("20260321" to "1700")
        kmaForecastClient.enqueueError("20260321", "1700", 59, 128, tooManyRequests())

        val exception =
            assertThrows(ErrorException::class.java) {
                weatherService.getTomorrowMaxPrecipitationProbability(location)
            }

        exception.errorType shouldBe ErrorType.EXCEED_RATE_LIMIT
    }

    @Test
    fun `upstream failure가 발생하면 최근 성공 캐시로 degrade 한다`() {
        val location = weatherLocation()
        val cachedResponse = weatherResponse(baseDate = "20260321", baseTime = "1400", probabilities = listOf(65))

        kmaForecastClient.enqueueBaseTime("20260321" to "1400")
        kmaForecastClient.enqueueBaseTime("20260321" to "1700")
        kmaForecastClient.enqueueResponse("20260321", "1400", 59, 128, cachedResponse)
        kmaForecastClient.enqueueError("20260321", "1700", 59, 128, IllegalStateException("upstream timeout"))

        weatherService.getTomorrowMaxPrecipitationProbability(location) shouldBe 65
        weatherService.getTomorrowMaxPrecipitationProbability(location) shouldBe 65
    }

    private fun tooManyRequests(): FeignException.TooManyRequests =
        FeignException.errorStatus(
            "getVilageForecast",
            Response
                .builder()
                .status(429)
                .reason("Too Many Requests")
                .request(
                    Request.create(
                        Request.HttpMethod.GET,
                        "http://localhost/getVilageFcst",
                        emptyMap(),
                        null,
                        Charsets.UTF_8,
                        null,
                    ),
                ).headers(emptyMap())
                .build(),
        ) as FeignException.TooManyRequests

    private fun weatherLocation(): WeatherLocation =
        WeatherLocation(
            latitude = 37.0,
            longitude = 127.0,
            nx = 59,
            ny = 128,
        )

    private fun weatherResponse(
        baseDate: String,
        baseTime: String,
        probabilities: List<Int>,
    ): KmaWeatherResponse =
        KmaWeatherResponse(
            response =
                KmaWeatherResponse.Response(
                    header = KmaWeatherResponse.Header(resultCode = "00", resultMsg = "NORMAL_SERVICE"),
                    body =
                        KmaWeatherResponse.Body(
                            dataType = "JSON",
                            items = KmaWeatherResponse.Items(item = weatherItems(baseDate, baseTime, probabilities)),
                            pageNo = 1,
                            numOfRows = 1000,
                            totalCount = probabilities.size,
                        ),
                ),
        )

    private fun weatherItems(
        baseDate: String,
        baseTime: String,
        probabilities: List<Int>,
    ): List<KmaWeatherResponse.Item> {
        val tomorrow = LocalDate.now().plusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE)

        return probabilities.mapIndexed { index, probability ->
            KmaWeatherResponse.Item(
                baseDate = baseDate,
                baseTime = baseTime,
                category = "POP",
                fcstDate = tomorrow,
                fcstTime = "${index + 9}00".padStart(4, '0'),
                fcstValue = probability.toString(),
                nx = 59,
                ny = 128,
            )
        }
    }

    private class FakeCacheRepository : CacheRepository {
        private val storage = mutableMapOf<String, String>()

        override fun get(key: String): String? = storage[key]

        override fun put(
            key: String,
            value: String,
            ttl: Long,
        ) {
            storage[key] = value
        }

        override fun delete(key: String) {
            storage.remove(key)
        }
    }

    private class FakeKmaForecastClient : KmaForecastClient {
        private val baseTimes = mutableListOf<Pair<String, String>>()
        private val responses = mutableMapOf<String, KmaWeatherResponse>()
        private val errors = mutableMapOf<String, Throwable>()
        private val requestCounts = mutableMapOf<String, Int>()

        fun enqueueBaseTime(baseTime: Pair<String, String>) {
            baseTimes += baseTime
        }

        fun enqueueResponse(
            baseDate: String,
            baseTime: String,
            nx: Int,
            ny: Int,
            response: KmaWeatherResponse,
        ) {
            responses[key(baseDate, baseTime, nx, ny)] = response
        }

        fun enqueueError(
            baseDate: String,
            baseTime: String,
            nx: Int,
            ny: Int,
            throwable: Throwable,
        ) {
            errors[key(baseDate, baseTime, nx, ny)] = throwable
        }

        fun requestCount(
            baseDate: String,
            baseTime: String,
            nx: Int,
            ny: Int,
        ): Int = requestCounts[key(baseDate, baseTime, nx, ny)] ?: 0

        override fun getVilageForecast(
            baseDate: String,
            baseTime: String,
            nx: Int,
            ny: Int,
            numOfRows: Int,
        ): KmaWeatherResponse {
            val key = key(baseDate, baseTime, nx, ny)
            requestCounts[key] = (requestCounts[key] ?: 0) + 1
            errors[key]?.let { throw it }
            return responses[key] ?: error("Missing response for $key")
        }

        override fun getLatestBaseTime(currentTime: LocalDateTime): Pair<String, String> {
            check(baseTimes.isNotEmpty()) { "Missing base time" }
            return baseTimes.removeAt(0)
        }

        private fun key(
            baseDate: String,
            baseTime: String,
            nx: Int,
            ny: Int,
        ): String = "$baseDate:$baseTime:$nx:$ny"
    }
}
