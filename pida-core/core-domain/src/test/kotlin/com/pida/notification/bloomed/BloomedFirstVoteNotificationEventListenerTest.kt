package com.pida.notification.bloomed

import com.pida.blooming.BloomingAddedEvent
import com.pida.blooming.BloomingStatus
import com.pida.blooming.NewBlooming
import com.pida.flowerevent.FlowerEvent
import com.pida.flowerevent.FlowerEventRepository
import com.pida.flowerspot.FlowerSpotCafeRepository
import com.pida.flowerspot.FlowerSpotRepository
import com.pida.support.geo.GeoJson
import com.pida.support.geo.Region
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.LocalDate

class BloomedFirstVoteNotificationEventListenerTest {
    @Test
    fun `꽃 이벤트 첫 만개 투표면 해당 지역 만개 알림을 발송한다`() {
        val flowerSpotRepository = mockk<FlowerSpotRepository>()
        val flowerEventRepository = mockk<FlowerEventRepository>()
        val flowerSpotCafeRepository = mockk<FlowerSpotCafeRepository>()
        val firstVoteChecker = mockk<BloomedFirstVoteChecker>()
        val bloomedNotificationService = mockk<BloomedNotificationService>()
        val listener =
            BloomedFirstVoteNotificationEventListener(
                flowerSpotRepository = flowerSpotRepository,
                flowerEventRepository = flowerEventRepository,
                flowerSpotCafeRepository = flowerSpotCafeRepository,
                firstVoteChecker = firstVoteChecker,
                bloomedNotificationService = bloomedNotificationService,
            )

        coEvery { flowerEventRepository.findBy(15L) } returns
            FlowerEvent(
                id = 15L,
                name = "석촌호수 벚꽃축제",
                address = "서울특별시 송파구 잠실동",
                pinPoint = GeoJson.Point(listOf(127.1040, 37.5070)),
                region = Region.SEOUL,
                homepageUrl = null,
                startDate = LocalDate.of(2026, 4, 1),
                endDate = LocalDate.of(2026, 4, 10),
                categoryId = 1L,
                deletedAt = null,
            )
        every { firstVoteChecker.isFirstBloomedVoteOfYear(Region.SEOUL) } returns true
        every { bloomedNotificationService.sendBloomedNotificationForRegion(Region.SEOUL) } returns 1

        listener.handleBloomingAddedEvent(
            BloomingAddedEvent(
                NewBlooming.FlowerEvent(
                    userId = 1L,
                    flowerEventId = 15L,
                    status = BloomingStatus.BLOOMED,
                ),
            ),
        )

        coVerify { flowerEventRepository.findBy(15L) }
        verify { bloomedNotificationService.sendBloomedNotificationForRegion(Region.SEOUL) }
    }
}
