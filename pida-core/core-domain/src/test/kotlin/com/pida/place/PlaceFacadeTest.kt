package com.pida.place

import com.pida.flowerspot.FlowerKind
import com.pida.flowerspot.FlowerSpot
import com.pida.flowerspot.FlowerSpotSearchEvent
import com.pida.flowerspot.FlowerSpotService
import com.pida.flowerspot.FlowerSpotType
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import com.pida.support.resilience.ExternalDependency
import com.pida.support.resilience.ExternalDependencyPolicy
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDateTime

class PlaceFacadeTest {
    @Test
    fun `Kakao Map breaker가 열려 있으면 저장된 landmark만 반환하고 보정 이벤트를 생략한다`(): Unit =
        runBlocking {
            val flowerSpotService = mockk<FlowerSpotService>()
            val landmarkService = mockk<LandmarkService>()
            val districtService = mockk<DistrictService>()
            val landmarkSearchClient = mockk<LandmarkSearchClient>()
            val eventPublisher = mockk<ApplicationEventPublisher>(relaxed = true)
            val policy = FakeExternalDependencyPolicy(setOf(ExternalDependency.KAKAO_MAP))

            val storedLandmark = landmark(id = 1L, name = "강남역")

            coEvery { districtService.searchDistricts("강남") } returns emptyList()
            coEvery { landmarkService.searchLandmarks("강남") } returns listOf(storedLandmark)
            coEvery { flowerSpotService.searchFlowerSpots("강남") } returns emptyList()

            val facade =
                PlaceFacade(
                    flowerSpotService = flowerSpotService,
                    landmarkService = landmarkService,
                    districtService = districtService,
                    landmarkSearchClient = landmarkSearchClient,
                    eventPublisher = eventPublisher,
                    externalDependencyPolicy = policy,
                )

            val result = facade.search("강남", null)

            result.landmarks shouldContainExactly listOf(storedLandmark)
            verify(exactly = 1) { eventPublisher.publishEvent(match { it is FlowerSpotSearchEvent }) }
            verify(exactly = 0) { landmarkSearchClient.searchByKeyword(any()) }
            verify(exactly = 0) { eventPublisher.publishEvent(match { it is LandmarkFetchEvent }) }
        }

    companion object {
        private fun landmark(
            id: Long,
            name: String,
        ) = Landmark(
            id = id,
            name = name,
            address = "서울 강남구",
            pinPoint = GeoJson.Point(listOf(127.0, 37.5)),
            region = Region.SEOUL,
            category = LandmarkCategory.SUBWAY,
            deletedAt = null,
        )

        @Suppress("unused")
        private fun flowerSpot(id: Long) =
            FlowerSpot(
                id = id,
                address = "서울특별시 강남구",
                streetName = "벚꽃길",
                district = "역삼동",
                description = "벚꽃길",
                geom = GeoJson.LineString(listOf(listOf(127.1, 37.5), listOf(127.2, 37.6))),
                pinPoint = GeoJson.Point(listOf(127.15, 37.55)),
                region = Region.SEOUL,
                kind = FlowerKind.BLOSSOM,
                type = FlowerSpotType.WALKING_TRAIL,
                deletedAt = null,
                previewImageKey = null,
                previewImageUploadedAt = LocalDateTime.now(),
            )
    }

    private class FakeExternalDependencyPolicy(
        private val unavailableDependencies: Set<ExternalDependency> = emptySet(),
    ) : ExternalDependencyPolicy {
        override fun isAvailable(dependency: ExternalDependency): Boolean = dependency !in unavailableDependencies

        override fun <T> execute(
            dependency: ExternalDependency,
            block: () -> T,
        ): T = block()

        override suspend fun <T> executeSuspend(
            dependency: ExternalDependency,
            block: suspend () -> T,
        ): T = block()

        override fun recordFallback(
            dependency: ExternalDependency,
            reason: String,
            throwable: Throwable?,
        ) {
        }
    }
}
