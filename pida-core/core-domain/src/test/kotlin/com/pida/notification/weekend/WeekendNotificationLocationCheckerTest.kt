package com.pida.notification.weekend

import com.pida.blooming.BloomingRepository
import com.pida.flowerevent.FlowerEvent
import com.pida.flowerevent.FlowerEventRepository
import com.pida.flowerspot.FlowerSpotRepository
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.time.LocalDate

class WeekendNotificationLocationCheckerTest {
    @Test
    fun `근처에 만개한 꽃 이벤트가 있으면 위치 조건을 통과한다`() {
        val flowerSpotRepository = mockk<FlowerSpotRepository>()
        val flowerEventRepository = mockk<FlowerEventRepository>()
        val bloomingRepository = mockk<BloomingRepository>()
        val checker =
            WeekendNotificationLocationChecker(
                flowerSpotRepository = flowerSpotRepository,
                flowerEventRepository = flowerEventRepository,
                bloomingRepository = bloomingRepository,
            )

        coEvery { flowerSpotRepository.findWithinRadius(37.5, 127.0, 3000.0) } returns emptyList()
        coEvery { flowerEventRepository.findWithinRadius(37.5, 127.0, 3000.0) } returns
            listOf(
                FlowerEvent(
                    id = 11L,
                    name = "여의도 봄꽃축제",
                    address = "서울특별시 영등포구 여의서로 330",
                    pinPoint = GeoJson.Point(listOf(126.9340, 37.5284)),
                    region = Region.SEOUL,
                    homepageUrl = null,
                    startDate = LocalDate.of(2026, 3, 20),
                    endDate = LocalDate.of(2026, 3, 30),
                    categoryId = 1L,
                    deletedAt = null,
                ),
            )
        every { bloomingRepository.findBloomedSpotIdsByFlowerSpotIds(emptyList()) } returns emptyList()
        every { bloomingRepository.findBloomedEventIdsByFlowerEventIds(listOf(11L)) } returns listOf(11L)

        val result = checker.hasNearbyBloomingLocations(37.5, 127.0)

        result shouldBe true
    }
}
