package com.pida.client.weather

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.pida.support.cache.Cache
import com.pida.support.cache.CacheRepository
import org.springframework.stereotype.Component

@Component
class KmaForecastCache(
    _cache: Cache,
    private val cacheRepository: CacheRepository,
    private val objectMapper: ObjectMapper,
) {
    companion object {
        private const val FORECAST_CACHE_TTL_MINUTES = 180L
        private const val STALE_GRID_CACHE_TTL_MINUTES = 360L
        private val FORECAST_ITEM_LIST_TYPE = object : TypeReference<List<KmaWeatherResponse.Item>>() {}
    }

    fun getOrLoad(
        baseDate: String,
        baseTime: String,
        nx: Int,
        ny: Int,
        loader: () -> List<KmaWeatherResponse.Item>,
    ): List<KmaWeatherResponse.Item> =
        Cache.cacheBlocking(
            ttl = FORECAST_CACHE_TTL_MINUTES,
            key = requestKey(baseDate, baseTime, nx, ny),
            typeReference = FORECAST_ITEM_LIST_TYPE,
        ) {
            loader().also { items ->
                putLatestByGrid(nx, ny, items)
            }
        }

    fun getLatestByGrid(
        nx: Int,
        ny: Int,
    ): List<KmaWeatherResponse.Item>? {
        val cached = cacheRepository.get(gridKey(nx, ny)) ?: return null
        return objectMapper.readValue(cached, FORECAST_ITEM_LIST_TYPE)
    }

    fun putLatestByGrid(
        nx: Int,
        ny: Int,
        items: List<KmaWeatherResponse.Item>,
    ) {
        cacheRepository.put(
            key = gridKey(nx, ny),
            value = objectMapper.writeValueAsString(items),
            ttl = STALE_GRID_CACHE_TTL_MINUTES,
        )
    }

    private fun requestKey(
        baseDate: String,
        baseTime: String,
        nx: Int,
        ny: Int,
    ): String = "kma:forecast:$baseDate:$baseTime:$nx:$ny"

    private fun gridKey(
        nx: Int,
        ny: Int,
    ): String = "kma:grid:$nx:$ny"
}
