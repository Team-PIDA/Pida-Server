package com.pida.notification.weekend

import com.pida.airquality.AirQualityService
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class WeekendNotificationAirQualityCheckerTest {
    @Test
    fun `대기질 조회가 실패해도 알림 차단을 막기 위해 true를 반환한다`() {
        val airQualityService = mockk<AirQualityService>()
        val checker = WeekendNotificationAirQualityChecker(airQualityService)

        every { airQualityService.getAirQuality(37.0, 127.0) } throws RuntimeException("boom")

        val result = checker.hasGoodAirQuality(37.0, 127.0)

        result shouldBe true
    }
}
