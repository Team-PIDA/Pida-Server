package com.pida.client.airquality

import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import com.pida.support.extension.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * 에어코리아 API 클라이언트
 */
@Component
class AirKoreaClient internal constructor(
    @param:Value("\${airkorea.api.service-key:}")
    private val serviceKey: String,
    private val airKoreaApi: AirKoreaApi,
) {
    private val logger by logger()

    /**
     * 측정소 이름으로 대기질 정보 조회
     *
     * @param stationName 측정소 이름
     * @return 대기질 정보
     */
    fun getAirQualityByStation(stationName: String): AirKoreaResponse =
        try {
            val response =
                airKoreaApi.getMsrstnAcctoRltmMesureDnsty(
                    serviceKey = serviceKey,
                    stationName = stationName,
                )

            if (response.response.header.resultCode != "00") {
                logger.error("AirKorea API error: ${response.response.header.resultMsg}")
                throw ErrorException(ErrorType.AIR_QUALITY_API_CALL_FAILED)
            }

            response
        } catch (e: ErrorException) {
            throw e
        } catch (e: Exception) {
            logger.error("Failed to fetch air quality data", e)
            throw ErrorException(ErrorType.AIR_QUALITY_API_CALL_FAILED)
        }

    /**
     * TM 좌표로 근접 측정소 조회
     *
     * @param tmX TM X 좌표
     * @param tmY TM Y 좌표
     * @return 가장 가까운 측정소 이름
     */
    fun getNearbyStation(
        tmX: String,
        tmY: String,
    ): String {
        logger.info("Finding nearby station for TM coordinates: ($tmX, $tmY)")

        return try {
            val response =
                airKoreaApi.getNearbyMsrstnList(
                    serviceKey = serviceKey,
                    tmX = tmX,
                    tmY = tmY,
                )

            if (response.response.header.resultCode != "00") {
                logger.error("AirKorea station API error: ${response.response.header.resultMsg}")
                throw ErrorException(ErrorType.AIR_QUALITY_STATION_NOT_FOUND)
            }

            val stations = response.response.body.items
            if (stations.isNullOrEmpty()) {
                throw ErrorException(ErrorType.AIR_QUALITY_STATION_NOT_FOUND)
            }

            // 가장 가까운 측정소 반환 (첫 번째 항목)
            stations.first().stationName
        } catch (e: ErrorException) {
            throw e
        } catch (e: Exception) {
            logger.error("Failed to find nearby station", e)
            throw ErrorException(ErrorType.AIR_QUALITY_STATION_NOT_FOUND)
        }
    }
}
