package com.pida.blooming

import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class BloomingServiceTest {
    @Test
    fun `flowerEventId가 주어지면 이벤트 기준으로 오늘 개화 상태를 검증한다`() {
        val bloomingAppender = mockk<BloomingAppender>()
        val bloomingValidator = mockk<BloomingValidator>()
        val bloomingFinder = mockk<BloomingFinder>()
        val service = BloomingService(bloomingAppender, bloomingValidator, bloomingFinder)
        val blooming =
            Blooming(
                id = 1L,
                status = BloomingStatus.BLOOMED,
                userId = 10L,
                flowerSpotId = null,
                flowerEventId = 20L,
                flowerSpotCafeId = null,
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
    fun `spot batch 조회는 중복 id를 제거한 뒤 한 번의 finder 호출로 위임한다`(): Unit =
        runBlocking {
            val bloomingAppender = mockk<BloomingAppender>()
            val bloomingValidator = mockk<BloomingValidator>()
            val bloomingFinder = mockk<BloomingFinder>()
            val service = BloomingService(bloomingAppender, bloomingValidator, bloomingFinder)
            val bloomings =
                listOf(
                    Blooming(
                        id = 1L,
                        status = BloomingStatus.BLOOMED,
                        userId = 10L,
                        flowerSpotId = 30L,
                        flowerEventId = null,
                        flowerSpotCafeId = null,
                        createdAt = LocalDateTime.of(2026, 4, 1, 9, 0),
                    ),
                )

            coEvery { bloomingFinder.recentlyBloomingBySpotIds(listOf(30L, 31L)) } returns bloomings

            val result = service.recentlyBloomingBySpotIds(listOf(30L, 31L, 30L))

            result shouldBe bloomings
            coVerify(exactly = 1) { bloomingFinder.recentlyBloomingBySpotIds(listOf(30L, 31L)) }
        }

    @Test
    fun `event batch 조회는 중복 id를 제거한 뒤 한 번의 finder 호출로 위임한다`(): Unit =
        runBlocking {
            val bloomingAppender = mockk<BloomingAppender>()
            val bloomingValidator = mockk<BloomingValidator>()
            val bloomingFinder = mockk<BloomingFinder>()
            val service = BloomingService(bloomingAppender, bloomingValidator, bloomingFinder)
            val bloomings =
                listOf(
                    Blooming(
                        id = 2L,
                        status = BloomingStatus.LITTLE,
                        userId = 11L,
                        flowerSpotId = null,
                        flowerEventId = 40L,
                        flowerSpotCafeId = null,
                        createdAt = LocalDateTime.of(2026, 4, 1, 10, 0),
                    ),
                )

            coEvery { bloomingFinder.recentlyBloomingByEventIds(listOf(40L, 41L)) } returns bloomings

            val result = service.recentlyBloomingByEventIds(listOf(40L, 41L, 40L))

            result shouldBe bloomings
            coVerify(exactly = 1) { bloomingFinder.recentlyBloomingByEventIds(listOf(40L, 41L)) }
        }

    @Test
    fun `cafe batch 조회는 중복 id를 제거한 뒤 한 번의 finder 호출로 위임한다`(): Unit =
        runBlocking {
            val bloomingAppender = mockk<BloomingAppender>()
            val bloomingValidator = mockk<BloomingValidator>()
            val bloomingFinder = mockk<BloomingFinder>()
            val service = BloomingService(bloomingAppender, bloomingValidator, bloomingFinder)
            val bloomings =
                listOf(
                    Blooming(
                        id = 3L,
                        status = BloomingStatus.WITHERED,
                        userId = 12L,
                        flowerSpotId = null,
                        flowerEventId = null,
                        flowerSpotCafeId = 50L,
                        createdAt = LocalDateTime.of(2026, 4, 1, 11, 0),
                    ),
                )

            coEvery { bloomingFinder.recentlyBloomingByCafeIds(listOf(50L, 51L)) } returns bloomings

            val result = service.recentlyBloomingByCafeIds(listOf(50L, 51L, 50L))

            result shouldBe bloomings
            coVerify(exactly = 1) { bloomingFinder.recentlyBloomingByCafeIds(listOf(50L, 51L)) }
        }

    @Test
    fun `flowerSpotId와 flowerEventId가 모두 없으면 INVALID_REQUEST를 던진다`() {
        val bloomingAppender = mockk<BloomingAppender>()
        val bloomingValidator = mockk<BloomingValidator>()
        val bloomingFinder = mockk<BloomingFinder>()
        val service = BloomingService(bloomingAppender, bloomingValidator, bloomingFinder)

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
        val service = BloomingService(bloomingAppender, bloomingValidator, bloomingFinder)

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
