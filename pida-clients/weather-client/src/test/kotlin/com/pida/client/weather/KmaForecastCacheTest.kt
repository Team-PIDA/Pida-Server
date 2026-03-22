package com.pida.client.weather

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pida.support.cache.Cache
import com.pida.support.cache.CacheAdvice
import com.pida.support.cache.CacheRepository
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class KmaForecastCacheTest {
    private val cacheRepository = FakeCacheRepository()
    private val objectMapper = jacksonObjectMapper()
    private val cache = Cache(CacheAdvice(cacheRepository, objectMapper))
    private val kmaForecastCache = KmaForecastCache(cache, cacheRepository, objectMapper)

    @Test
    fun `같은 base 시각과 격자에 대한 요청은 Redis 캐시를 재사용한다`() {
        var loadCount = 0

        val first =
            kmaForecastCache.getOrLoad("20260321", "1700", 59, 128) {
                loadCount += 1
                weatherItems(baseDate = "20260321", baseTime = "1700", probabilities = listOf(80))
            }

        val second =
            kmaForecastCache.getOrLoad("20260321", "1700", 59, 128) {
                loadCount += 1
                weatherItems(baseDate = "20260321", baseTime = "1700", probabilities = listOf(20))
            }

        loadCount shouldBe 1
        first.first().fcstValue shouldBe "80"
        second.first().fcstValue shouldBe "80"
    }

    @Test
    fun `성공한 예보는 최근 격자 캐시에도 저장한다`() {
        val items =
            kmaForecastCache.getOrLoad("20260321", "1700", 59, 128) {
                weatherItems(baseDate = "20260321", baseTime = "1700", probabilities = listOf(70))
            }

        kmaForecastCache.getLatestByGrid(59, 128) shouldBe items
    }

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
}
