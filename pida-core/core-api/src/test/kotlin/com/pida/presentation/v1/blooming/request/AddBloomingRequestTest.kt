package com.pida.presentation.v1.blooming.request

import com.pida.blooming.BloomingStatus
import com.pida.blooming.NewBlooming
import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AddBloomingRequestTest {
    @Test
    fun `flowerSpotId만 있으면 FlowerSpot 개화 입력으로 변환한다`() {
        val request =
            AddBloomingRequest(
                flowerSpotId = 1L,
                flowerEventId = null,
                status = BloomingStatus.BLOOMED,
            )

        val result = request.toNewBlooming(10L)

        result.shouldBeInstanceOf<NewBlooming.FlowerSpot>()
        result.userId shouldBe 10L
        result.flowerSpotId shouldBe 1L
        result.flowerEventId shouldBe null
        result.status shouldBe BloomingStatus.BLOOMED
    }

    @Test
    fun `flowerEventId만 있으면 FlowerEvent 개화 입력으로 변환한다`() {
        val request =
            AddBloomingRequest(
                flowerSpotId = null,
                flowerEventId = 2L,
                status = BloomingStatus.WITHERED,
            )

        val result = request.toNewBlooming(11L)

        result.shouldBeInstanceOf<NewBlooming.FlowerEvent>()
        result.userId shouldBe 11L
        result.flowerSpotId shouldBe null
        result.flowerEventId shouldBe 2L
        result.status shouldBe BloomingStatus.WITHERED
    }

    @Test
    fun `flowerSpotId와 flowerEventId가 모두 없으면 INVALID_REQUEST를 던진다`() {
        val request =
            AddBloomingRequest(
                flowerSpotId = null,
                flowerEventId = null,
                status = BloomingStatus.BLOOMED,
            )

        val exception = assertThrows<ErrorException> { request.toNewBlooming(12L) }

        exception.errorType shouldBe ErrorType.INVALID_REQUEST
    }

    @Test
    fun `flowerSpotId와 flowerEventId가 모두 있으면 INVALID_REQUEST를 던진다`() {
        val request =
            AddBloomingRequest(
                flowerSpotId = 1L,
                flowerEventId = 2L,
                status = BloomingStatus.BLOOMED,
            )

        val exception = assertThrows<ErrorException> { request.toNewBlooming(13L) }

        exception.errorType shouldBe ErrorType.INVALID_REQUEST
    }
}
