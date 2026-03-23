package com.pida.client.airquality

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class AirQualityServiceImplTest {
    @Test
    fun `대기질 응답의 stationName이 없어도 조회된 측정소명을 사용한다`() {
        val airKoreaClient = mockk<AirKoreaClient>()
        val service = AirQualityServiceImpl(airKoreaClient)

        every { airKoreaClient.getNearbyStation(any(), any()) } returns "종로구"
        every { airKoreaClient.getAirQualityByStation("종로구") } returns
            AirKoreaResponse(
                response =
                    AirKoreaResponse.Response(
                        header = AirKoreaResponse.Header(resultCode = "00", resultMsg = "NORMAL SERVICE"),
                        body =
                            AirKoreaResponse.Body(
                                items =
                                    listOf(
                                        AirKoreaResponse.Item(
                                            stationName = null,
                                            dataTime = "2026-03-20 18:00",
                                            pm10Value = "20",
                                            pm25Value = "10",
                                            khaiValue = null,
                                            so2Value = null,
                                            coValue = null,
                                            o3Value = null,
                                            no2Value = null,
                                        ),
                                    ),
                                numOfRows = 1,
                                pageNo = 1,
                                totalCount = 1,
                            ),
                    ),
            )

        val result = service.getAirQuality(latitude = 37.572025, longitude = 127.005028)

        result.stationName shouldBe "종로구"
        result.pm10 shouldBe 20
        result.pm25 shouldBe 10
        result.measurementTime shouldBe LocalDateTime.of(2026, 3, 20, 18, 0)
    }
}
