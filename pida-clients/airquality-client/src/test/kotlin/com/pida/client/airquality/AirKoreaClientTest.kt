package com.pida.client.airquality

import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AirKoreaClientTest {
    @Test
    fun `근접 측정소 조회 성공 시 첫 번째 측정소명을 반환한다`() {
        val airKoreaAirQualityApi = mockk<AirKoreaAirQualityApi>()
        val airKoreaStationApi = mockk<AirKoreaStationApi>()
        val client = AirKoreaClient("air-quality-key", "station-key", airKoreaAirQualityApi, airKoreaStationApi)

        every {
            airKoreaStationApi.getNearbyMsrstnList(
                serviceKey = "station-key",
                tmX = "192968",
                tmY = "4667503",
            )
        } returns
            AirKoreaStationResponse(
                response =
                    AirKoreaStationResponse.Response(
                        header = AirKoreaStationResponse.Header(resultCode = "00", resultMsg = "NORMAL SERVICE"),
                        body =
                            AirKoreaStationResponse.Body(
                                items =
                                    listOf(
                                        AirKoreaStationResponse.StationItem(
                                            stationName = "종로구",
                                            addr = "서울 종로구",
                                            tm = 0.1,
                                        ),
                                        AirKoreaStationResponse.StationItem(
                                            stationName = "중구",
                                            addr = "서울 중구",
                                            tm = 0.2,
                                        ),
                                    ),
                                numOfRows = 2,
                                pageNo = 1,
                                totalCount = 2,
                            ),
                    ),
            )

        val result = client.getNearbyStation("192968", "4667503")

        result shouldBe "종로구"
    }

    @Test
    fun `근접 측정소 조회 결과 코드가 실패면 AIR_QUALITY_STATION_NOT_FOUND를 던진다`() {
        val airKoreaAirQualityApi = mockk<AirKoreaAirQualityApi>()
        val airKoreaStationApi = mockk<AirKoreaStationApi>()
        val client = AirKoreaClient("air-quality-key", "station-key", airKoreaAirQualityApi, airKoreaStationApi)

        every {
            airKoreaStationApi.getNearbyMsrstnList(
                serviceKey = "station-key",
                tmX = "192968",
                tmY = "4667503",
            )
        } returns
            AirKoreaStationResponse(
                response =
                    AirKoreaStationResponse.Response(
                        header = AirKoreaStationResponse.Header(resultCode = "99", resultMsg = "API ERROR"),
                        body = AirKoreaStationResponse.Body(items = emptyList(), numOfRows = 0, pageNo = 1, totalCount = 0),
                    ),
            )

        val exception =
            assertThrows<ErrorException> {
                client.getNearbyStation("192968", "4667503")
            }

        exception.errorType shouldBe ErrorType.AIR_QUALITY_STATION_NOT_FOUND
    }

    @Test
    fun `근접 측정소 조회 결과가 비어 있으면 AIR_QUALITY_STATION_NOT_FOUND를 던진다`() {
        val airKoreaAirQualityApi = mockk<AirKoreaAirQualityApi>()
        val airKoreaStationApi = mockk<AirKoreaStationApi>()
        val client = AirKoreaClient("air-quality-key", "station-key", airKoreaAirQualityApi, airKoreaStationApi)

        every {
            airKoreaStationApi.getNearbyMsrstnList(
                serviceKey = "station-key",
                tmX = "192968",
                tmY = "4667503",
            )
        } returns
            AirKoreaStationResponse(
                response =
                    AirKoreaStationResponse.Response(
                        header = AirKoreaStationResponse.Header(resultCode = "00", resultMsg = "NORMAL SERVICE"),
                        body = AirKoreaStationResponse.Body(items = emptyList(), numOfRows = 0, pageNo = 1, totalCount = 0),
                    ),
            )

        val exception =
            assertThrows<ErrorException> {
                client.getNearbyStation("192968", "4667503")
            }

        exception.errorType shouldBe ErrorType.AIR_QUALITY_STATION_NOT_FOUND
    }

    @Test
    fun `근접 측정소 조회 중 예외가 발생하면 AIR_QUALITY_STATION_NOT_FOUND를 던진다`() {
        val airKoreaAirQualityApi = mockk<AirKoreaAirQualityApi>()
        val airKoreaStationApi = mockk<AirKoreaStationApi>()
        val client = AirKoreaClient("air-quality-key", "station-key", airKoreaAirQualityApi, airKoreaStationApi)

        every {
            airKoreaStationApi.getNearbyMsrstnList(
                serviceKey = "station-key",
                tmX = "192968",
                tmY = "4667503",
            )
        } throws RuntimeException("boom")

        val exception =
            assertThrows<ErrorException> {
                client.getNearbyStation("192968", "4667503")
            }

        exception.errorType shouldBe ErrorType.AIR_QUALITY_STATION_NOT_FOUND
    }

    @Test
    fun `대기질 조회 결과 코드가 실패면 AIR_QUALITY_API_CALL_FAILED를 던진다`() {
        val airKoreaAirQualityApi = mockk<AirKoreaAirQualityApi>()
        val airKoreaStationApi = mockk<AirKoreaStationApi>()
        val client = AirKoreaClient("air-quality-key", "station-key", airKoreaAirQualityApi, airKoreaStationApi)

        every {
            airKoreaAirQualityApi.getMsrstnAcctoRltmMesureDnsty(
                serviceKey = "air-quality-key",
                stationName = "종로구",
            )
        } returns
            AirKoreaResponse(
                response =
                    AirKoreaResponse.Response(
                        header = AirKoreaResponse.Header(resultCode = "99", resultMsg = "API ERROR"),
                        body = AirKoreaResponse.Body(items = emptyList(), numOfRows = 0, pageNo = 1, totalCount = 0),
                    ),
            )

        val exception =
            assertThrows<ErrorException> {
                client.getAirQualityByStation("종로구")
            }

        exception.errorType shouldBe ErrorType.AIR_QUALITY_API_CALL_FAILED
    }

    @Test
    fun `대기질 조회 중 예외가 발생하면 AIR_QUALITY_API_CALL_FAILED를 던진다`() {
        val airKoreaAirQualityApi = mockk<AirKoreaAirQualityApi>()
        val airKoreaStationApi = mockk<AirKoreaStationApi>()
        val client = AirKoreaClient("air-quality-key", "station-key", airKoreaAirQualityApi, airKoreaStationApi)

        every {
            airKoreaAirQualityApi.getMsrstnAcctoRltmMesureDnsty(
                serviceKey = "air-quality-key",
                stationName = "종로구",
            )
        } throws RuntimeException("boom")

        val exception =
            assertThrows<ErrorException> {
                client.getAirQualityByStation("종로구")
            }

        exception.errorType shouldBe ErrorType.AIR_QUALITY_API_CALL_FAILED
    }

    @Test
    fun `메서드별로 서로 다른 service key를 사용한다`() {
        val airKoreaAirQualityApi = mockk<AirKoreaAirQualityApi>()
        val airKoreaStationApi = mockk<AirKoreaStationApi>()
        val client = AirKoreaClient("air-quality-key", "station-key", airKoreaAirQualityApi, airKoreaStationApi)

        every {
            airKoreaStationApi.getNearbyMsrstnList(
                serviceKey = "station-key",
                tmX = "192968",
                tmY = "4667503",
            )
        } returns
            AirKoreaStationResponse(
                response =
                    AirKoreaStationResponse.Response(
                        header = AirKoreaStationResponse.Header(resultCode = "00", resultMsg = "NORMAL SERVICE"),
                        body =
                            AirKoreaStationResponse.Body(
                                items =
                                    listOf(
                                        AirKoreaStationResponse.StationItem(
                                            stationName = "종로구",
                                            addr = "서울 종로구",
                                            tm = 0.1,
                                        ),
                                    ),
                                numOfRows = 1,
                                pageNo = 1,
                                totalCount = 1,
                            ),
                    ),
            )
        every {
            airKoreaAirQualityApi.getMsrstnAcctoRltmMesureDnsty(
                serviceKey = "air-quality-key",
                stationName = "종로구",
            )
        } returns
            AirKoreaResponse(
                response =
                    AirKoreaResponse.Response(
                        header = AirKoreaResponse.Header(resultCode = "00", resultMsg = "NORMAL SERVICE"),
                        body =
                            AirKoreaResponse.Body(
                                items =
                                    listOf(
                                        AirKoreaResponse.Item(
                                            stationName = "종로구",
                                            dataTime = "2026-03-19 18:00",
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

        client.getNearbyStation("192968", "4667503")
        client.getAirQualityByStation("종로구")
    }
}
