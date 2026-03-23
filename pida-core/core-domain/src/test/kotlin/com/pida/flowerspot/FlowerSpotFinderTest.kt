package com.pida.flowerspot

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pida.support.cache.CacheAdvice
import com.pida.support.cache.CacheRepository
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class FlowerSpotFinderTest {
    @Test
    fun `위치와 지역 조회는 전체 캐시 데이터에서 필터링한다`(): Unit =
        runBlocking {
            val flowerSpotRepository = mockk<FlowerSpotRepository>()
            val cacheAdvice = CacheAdvice(InMemoryCacheRepository(), jacksonObjectMapper().findAndRegisterModules())
            val finder = FlowerSpotFinder(flowerSpotRepository, cacheAdvice)
            val location = FlowerSpotLocation(swLat = 37.4, swLng = 126.9, neLat = 37.6, neLng = 127.2)
            val seoulInBounds = flowerSpot(id = 1L, region = Region.SEOUL, longitude = 127.0, latitude = 37.5)
            val seoulOutOfBounds = flowerSpot(id = 2L, region = Region.SEOUL, longitude = 127.4, latitude = 37.7)
            val busanInBounds = flowerSpot(id = 3L, region = Region.BUSAN, longitude = 127.1, latitude = 37.5)

            coEvery { flowerSpotRepository.findAll() } returns listOf(seoulInBounds, seoulOutOfBounds, busanInBounds)

            val byRegionAndLocation = finder.readAllByLocationAndRegion(Region.SEOUL, location)
            val byLocation = finder.readAllByLocation(location)

            byRegionAndLocation shouldContainExactly listOf(seoulInBounds)
            byLocation shouldContainExactly listOf(seoulInBounds, busanInBounds)

            coVerify(exactly = 1) { flowerSpotRepository.findAll() }
            coVerify(exactly = 0) { flowerSpotRepository.findAllByRegion(any()) }
            coVerify(exactly = 0) { flowerSpotRepository.findAllByLocation(any()) }
            coVerify(exactly = 0) { flowerSpotRepository.findAllByLocationAndRegion(any(), any()) }
        }

    private fun flowerSpot(
        id: Long,
        region: Region,
        longitude: Double,
        latitude: Double,
    ) = FlowerSpot(
        id = id,
        address = "주소-$id",
        streetName = "거리-$id",
        district = "행정동-$id",
        description = "설명-$id",
        geom = GeoJson.LineString(listOf(listOf(longitude, latitude), listOf(longitude + 0.001, latitude + 0.001))),
        pinPoint = GeoJson.Point(listOf(longitude, latitude)),
        region = region,
        kind = FlowerKind.BLOSSOM,
        type = FlowerSpotType.WALKING_TRAIL,
        deletedAt = null,
    )
}

private class InMemoryCacheRepository : CacheRepository {
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
