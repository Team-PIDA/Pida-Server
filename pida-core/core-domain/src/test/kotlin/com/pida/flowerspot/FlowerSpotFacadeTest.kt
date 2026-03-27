package com.pida.flowerspot

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.pida.blooming.Blooming
import com.pida.blooming.BloomingService
import com.pida.blooming.BloomingStatus
import com.pida.support.aws.ImageS3Caller
import com.pida.support.cache.Cache
import com.pida.support.cache.CacheAdvice
import com.pida.support.cache.CacheRepository
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class FlowerSpotFacadeTest {
    @Test
    fun `목록 조회는 preview 이미지와 미리 계산한 bloomings를 사용한다`(): Unit =
        runBlocking {
            val flowerSpotService = mockk<FlowerSpotService>()
            val bloomingService = mockk<BloomingService>()
            val imageS3Caller = mockk<ImageS3Caller>()
            Cache(CacheAdvice(inMemoryCacheRepository(), cacheObjectMapper()))
            val facade = FlowerSpotFacade(flowerSpotService, bloomingService, imageS3Caller)
            val location = FlowerSpotLocation(swLat = null, swLng = null, neLat = null, neLng = null)
            val previewKey = "prod/flowerspot/10/abc.jpeg"
            val previewUploadedAt = LocalDateTime.of(2026, 3, 20, 12, 0)
            val firstSpot = flowerSpot(id = 10L, streetName = "첫 번째 거리", previewImageKey = previewKey, previewImageUploadedAt = previewUploadedAt)
            val secondSpot = flowerSpot(id = 20L, streetName = "두 번째 거리")

            coEvery { flowerSpotService.readAllFlowerSpot(region = null, location = location) } returns listOf(firstSpot, secondSpot)
            every { bloomingService.recentlyBloomingBySpotIds(listOf(10L, 20L)) } returns
                listOf(
                    Blooming(
                        id = 1L,
                        userId = 1L,
                        flowerSpotId = 10L,
                        flowerEventId = null,
                        status = BloomingStatus.BLOOMED,
                        createdAt = LocalDateTime.of(2026, 3, 20, 10, 0),
                    ),
                    Blooming(
                        id = 2L,
                        userId = 2L,
                        flowerSpotId = 10L,
                        flowerEventId = null,
                        status = BloomingStatus.BLOOMED,
                        createdAt = LocalDateTime.of(2026, 3, 20, 11, 0),
                    ),
                    Blooming(
                        id = 3L,
                        userId = 3L,
                        flowerSpotId = 20L,
                        flowerEventId = null,
                        status = BloomingStatus.LITTLE,
                        createdAt = LocalDateTime.of(2026, 3, 20, 9, 0),
                    ),
                )
            every { imageS3Caller.generatePresignedUrl(previewKey) } returns "https://cdn.example.com/flower-spot-10-preview.jpg"

            val result = facade.findAllFlowerSpot(region = null, location = location)

            result.map { it.id } shouldContainExactly listOf(10L, 20L)
            result.first().recentlyVisitedCount shouldBe 2L
            result.first().bloomingStatus shouldBe BloomingStatus.BLOOMED
            result.first().images.map { it.url } shouldContainExactly listOf("https://cdn.example.com/flower-spot-10-preview.jpg")
            result.last().recentlyVisitedCount shouldBe 1L
            result.last().bloomingStatus shouldBe BloomingStatus.LITTLE
            result.last().images shouldBe emptyList()

            verify(exactly = 1) { imageS3Caller.generatePresignedUrl(previewKey) }
            coVerify(exactly = 0) { imageS3Caller.getPreviewImage(any(), any()) }
            coVerify(exactly = 0) { imageS3Caller.getImageUrl(any(), any(), any()) }
        }

    @Test
    fun `목록 조회 결과가 비어 있으면 추가 조회를 생략한다`(): Unit =
        runBlocking {
            val flowerSpotService = mockk<FlowerSpotService>()
            val bloomingService = mockk<BloomingService>()
            val imageS3Caller = mockk<ImageS3Caller>()
            Cache(CacheAdvice(inMemoryCacheRepository(), cacheObjectMapper()))
            val facade = FlowerSpotFacade(flowerSpotService, bloomingService, imageS3Caller)
            val location = FlowerSpotLocation(swLat = null, swLng = null, neLat = null, neLng = null)

            coEvery { flowerSpotService.readAllFlowerSpot(region = null, location = location) } returns emptyList()

            val result = facade.findAllFlowerSpot(region = null, location = location)

            result shouldBe emptyList()
            verify(exactly = 0) { bloomingService.recentlyBloomingBySpotIds(any()) }
            coVerify(exactly = 0) { imageS3Caller.getPreviewImage(any(), any()) }
            coVerify(exactly = 0) { imageS3Caller.getImageUrl(any(), any(), any()) }
        }

    private fun flowerSpot(
        id: Long,
        streetName: String,
        previewImageKey: String? = null,
        previewImageUploadedAt: LocalDateTime? = null,
    ) = FlowerSpot(
        id = id,
        address = "서울특별시 강남구",
        streetName = streetName,
        district = "역삼동",
        description = "벚꽃길",
        geom = GeoJson.LineString(listOf(listOf(127.1, 37.5), listOf(127.2, 37.6))),
        pinPoint = GeoJson.Point(listOf(127.15, 37.55)),
        region = Region.SEOUL,
        kind = FlowerKind.BLOSSOM,
        type = FlowerSpotType.WALKING_TRAIL,
        deletedAt = null,
        previewImageKey = previewImageKey,
        previewImageUploadedAt = previewImageUploadedAt,
    )

    private fun inMemoryCacheRepository(): CacheRepository =
        object : CacheRepository {
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

    private fun cacheObjectMapper() =
        jacksonObjectMapper().registerModule(
            SimpleModule().addSerializer(LocalDateTime::class.java, ToStringSerializer.instance),
        )
}
