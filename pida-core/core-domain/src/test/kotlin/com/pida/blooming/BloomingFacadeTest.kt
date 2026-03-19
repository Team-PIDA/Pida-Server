package com.pida.blooming

import com.pida.reporter.RecentReporterService
import com.pida.support.aws.ImagePrefix
import com.pida.support.aws.ImageS3Caller
import com.pida.support.aws.S3ImageUrl
import com.pida.user.UserProfile
import com.pida.user.UserService
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDateTime

class BloomingFacadeTest {
    @Test
    fun `꽃 이벤트 개화 상태 상세 조회는 최근 이벤트 제보를 집계한다`() {
        runBlocking {
            val bloomingService = mockk<BloomingService>()
            val recentReporterService = mockk<RecentReporterService>()
            val userService = mockk<UserService>()
            val imageS3Caller = mockk<ImageS3Caller>()
            val eventPublisher = mockk<ApplicationEventPublisher>()
            val facade =
                BloomingFacade(
                    bloomingService = bloomingService,
                    recentReporterService = recentReporterService,
                    userService = userService,
                    imageS3Caller = imageS3Caller,
                    eventPublisher = eventPublisher,
                )

            coEvery { bloomingService.recentlyBloomingByEventId(7L) } returns
                listOf(
                    Blooming(
                        id = 1L,
                        status = BloomingStatus.LITTLE,
                        userId = 2L,
                        flowerSpotId = null,
                        flowerEventId = 7L,
                        createdAt = LocalDateTime.of(2026, 3, 18, 10, 0),
                    ),
                    Blooming(
                        id = 2L,
                        status = BloomingStatus.BLOOMED,
                        userId = 3L,
                        flowerSpotId = null,
                        flowerEventId = 7L,
                        createdAt = LocalDateTime.of(2026, 3, 19, 11, 0),
                    ),
                    Blooming(
                        id = 3L,
                        status = BloomingStatus.BLOOMED,
                        userId = 4L,
                        flowerSpotId = null,
                        flowerEventId = 7L,
                        createdAt = LocalDateTime.of(2026, 3, 19, 9, 0),
                    ),
                )
            coEvery { userService.getProfile(3L) } returns
                UserProfile(
                    id = 3L,
                    key = "user-3",
                    email = "user3@test.com",
                    name = "tester",
                    nickname = "봄길러",
                    createdAt = LocalDateTime.of(2026, 3, 1, 0, 0),
                )

            val result = facade.readBloomingDetails(flowerEventId = 7L)

            result.totalCount shouldBe 3L
            result.nickname shouldBe "봄길러"
            result.updatedAt shouldBe LocalDateTime.of(2026, 3, 19, 11, 0)
            result.details["2026-03-19"]?.get(BloomingStatus.BLOOMED.name)?.peopleCount shouldBe 2
            result.details["2026-03-19"]?.get(BloomingStatus.BLOOMED.name)?.percentage shouldBe 100
            result.details["2026-03-18"]?.get(BloomingStatus.LITTLE.name)?.peopleCount shouldBe 1
        }
    }

    @Test
    fun `꽃 명소 개화 상태 업로드는 flowerspot prefix로 presigned url을 생성한다`() {
        runBlocking {
            val bloomingService = mockk<BloomingService>()
            val recentReporterService = mockk<RecentReporterService>()
            val userService = mockk<UserService>()
            val imageS3Caller = mockk<ImageS3Caller>()
            val eventPublisher = mockk<ApplicationEventPublisher>()
            val facade =
                BloomingFacade(
                    bloomingService = bloomingService,
                    recentReporterService = recentReporterService,
                    userService = userService,
                    imageS3Caller = imageS3Caller,
                    eventPublisher = eventPublisher,
                )
            val newBlooming =
                NewBlooming.FlowerSpot(
                    userId = 1L,
                    flowerSpotId = 3L,
                    status = BloomingStatus.BLOOMED,
                )

            coEvery { bloomingService.add(newBlooming) } returns
                Blooming(
                    id = 1L,
                    status = BloomingStatus.BLOOMED,
                    userId = 1L,
                    flowerSpotId = 3L,
                    flowerEventId = null,
                    createdAt = LocalDateTime.of(2026, 3, 19, 10, 0),
                )
            every { eventPublisher.publishEvent(BloomingAddedEvent(newBlooming)) } returns Unit
            every {
                imageS3Caller.createUploadUrl(1L, ImagePrefix.FLOWERSPOT.value, 3L)
            } returns
                S3ImageUrl(
                    presignedUrl = "spot-upload",
                    presignedGetUrl = "spot-preview",
                )

            val result = facade.uploadBloomingStatus(newBlooming)

            result.uploadUrl shouldBe "spot-upload"
            result.previewUrl shouldBe "spot-preview"
            verify { eventPublisher.publishEvent(BloomingAddedEvent(newBlooming)) }
            verify { imageS3Caller.createUploadUrl(1L, ImagePrefix.FLOWERSPOT.value, 3L) }
        }
    }

    @Test
    fun `꽃 이벤트 개화 상태 업로드는 flowerevent prefix로 presigned url을 생성한다`() {
        runBlocking {
            val bloomingService = mockk<BloomingService>()
            val recentReporterService = mockk<RecentReporterService>()
            val userService = mockk<UserService>()
            val imageS3Caller = mockk<ImageS3Caller>()
            val eventPublisher = mockk<ApplicationEventPublisher>()
            val facade =
                BloomingFacade(
                    bloomingService = bloomingService,
                    recentReporterService = recentReporterService,
                    userService = userService,
                    imageS3Caller = imageS3Caller,
                    eventPublisher = eventPublisher,
                )
            val newBlooming =
                NewBlooming.FlowerEvent(
                    userId = 2L,
                    flowerEventId = 7L,
                    status = BloomingStatus.BLOOMED,
                )

            coEvery { bloomingService.add(newBlooming) } returns
                Blooming(
                    id = 2L,
                    status = BloomingStatus.BLOOMED,
                    userId = 2L,
                    flowerSpotId = null,
                    flowerEventId = 7L,
                    createdAt = LocalDateTime.of(2026, 3, 19, 11, 0),
                )
            every { eventPublisher.publishEvent(BloomingAddedEvent(newBlooming)) } returns Unit
            every {
                imageS3Caller.createUploadUrl(2L, ImagePrefix.FLOWEREVENT.value, 7L)
            } returns
                S3ImageUrl(
                    presignedUrl = "event-upload",
                    presignedGetUrl = "event-preview",
                )

            val result = facade.uploadBloomingStatus(newBlooming)

            result.uploadUrl shouldBe "event-upload"
            result.previewUrl shouldBe "event-preview"
            verify { eventPublisher.publishEvent(BloomingAddedEvent(newBlooming)) }
            verify { imageS3Caller.createUploadUrl(2L, ImagePrefix.FLOWEREVENT.value, 7L) }
        }
    }
}
