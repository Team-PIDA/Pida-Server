package com.pida.blooming

import com.pida.support.cache.CacheRepository
import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class BloomingServiceTest {
    @Test
    fun `flowerEventId가 주어지면 이벤트 기준으로 오늘 개화 상태를 검증한다`() {
        val bloomingAppender = mockk<BloomingAppender>()
        val bloomingValidator = mockk<BloomingValidator>()
        val bloomingFinder = mockk<BloomingFinder>()
        val cacheRepository = mockk<CacheRepository>(relaxed = true)
        val service = BloomingService(bloomingAppender, bloomingValidator, bloomingFinder, cacheRepository)
        val blooming =
            Blooming(
                id = 1L,
                status = BloomingStatus.BLOOMED,
                userId = 10L,
                flowerSpotId = null,
                flowerEventId = 20L,
                createdAt = LocalDateTime.of(2026, 3, 19, 9, 0),
            )

        every { bloomingFinder.readTodayBloomingByUserIdAndFlowerEventId(10L, 20L) } returns blooming
        every { bloomingValidator.todayBloomingValidate(blooming) } returns true

        val result =
            service.verifyTodayBlooming(
                userId = 10L,
                flowerEventId = 20L,
            )

        result shouldBe true
        verify { bloomingFinder.readTodayBloomingByUserIdAndFlowerEventId(10L, 20L) }
        verify { bloomingValidator.todayBloomingValidate(blooming) }
    }

    @Test
    fun `flowerSpotId와 flowerEventId가 모두 없으면 INVALID_REQUEST를 던진다`() {
        val bloomingAppender = mockk<BloomingAppender>()
        val bloomingValidator = mockk<BloomingValidator>()
        val bloomingFinder = mockk<BloomingFinder>()
        val cacheRepository = mockk<CacheRepository>(relaxed = true)
        val service = BloomingService(bloomingAppender, bloomingValidator, bloomingFinder, cacheRepository)

        val exception =
            assertThrows<ErrorException> {
                service.verifyTodayBlooming(
                    userId = 10L,
                )
            }

        exception.errorType shouldBe ErrorType.INVALID_REQUEST
    }

    @Test
    fun `flowerSpotId와 flowerEventId가 모두 있으면 INVALID_REQUEST를 던진다`() {
        val bloomingAppender = mockk<BloomingAppender>()
        val bloomingValidator = mockk<BloomingValidator>()
        val bloomingFinder = mockk<BloomingFinder>()
        val cacheRepository = mockk<CacheRepository>(relaxed = true)
        val service = BloomingService(bloomingAppender, bloomingValidator, bloomingFinder, cacheRepository)

        val exception =
            assertThrows<ErrorException> {
                service.verifyTodayBlooming(
                    userId = 10L,
                    flowerSpotId = 1L,
                    flowerEventId = 20L,
                )
            }

        exception.errorType shouldBe ErrorType.INVALID_REQUEST
    }
}
