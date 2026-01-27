package com.pida.client.airquality

import com.pida.airquality.AirQuality
import com.pida.airquality.AirQualityService
import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import com.pida.support.extension.logger
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * 대기질 서비스 구현체
 */
@Service
class AirQualityServiceImpl(
    private val airKoreaClient: AirKoreaClient,
) : AirQualityService {
    private val logger by logger()

    override fun getAirQuality(
        latitude: Double,
        longitude: Double,
    ): AirQuality {
        try {
            // 1. WGS84 좌표를 TM 좌표로 변환
            val (tmX, tmY) = convertWgs84ToTm(latitude, longitude)

            // 2. 근처 측정소 찾기
            val stationName = airKoreaClient.getNearbyStation(tmX, tmY)

            // 3. 측정소의 대기질 정보 조회
            val response = airKoreaClient.getAirQualityByStation(stationName)

            val items = response.response.body.items
            if (items.isNullOrEmpty()) {
                throw ErrorException(ErrorType.AIR_QUALITY_DATA_NOT_AVAILABLE)
            }

            val item = items.first()

            return AirQuality(
                pm10 = item.pm10Value?.toIntOrNull() ?: 0,
                pm25 = item.pm25Value?.toIntOrNull() ?: 0,
                measurementTime = parseDataTime(item.dataTime),
                stationName = item.stationName,
            )
        } catch (e: ErrorException) {
            throw e
        } catch (e: Exception) {
            logger.error("Failed to get air quality for ($latitude, $longitude)", e)
            throw ErrorException(ErrorType.AIR_QUALITY_API_CALL_FAILED)
        }
    }

    /**
     * 측정 일시 파싱 (yyyy-MM-dd HH:mm -> LocalDateTime)
     */
    private fun parseDataTime(dataTime: String): LocalDateTime =
        LocalDateTime.parse(
            dataTime,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
        )

    /**
     * WGS84 좌표를 TM (중부원점) 좌표로 변환
     *
     * 참고: 정확한 변환을 위해서는 GeoTools 같은 라이브러리 사용 권장
     * 여기서는 단순화된 변환 공식 사용
     */
    private fun convertWgs84ToTm(
        latitude: Double,
        longitude: Double,
    ): Pair<String, String> {
        // TM 중부원점 (EPSG:2097) 파라미터
        val falseNorthing = 500000.0
        val falseEasting = 200000.0
        val scaleFactor = 1.0
        val centralMeridian = 127.0 // 중부원점 경도

        // 각도를 라디안으로 변환
        val lat = Math.toRadians(latitude)
        val lon = Math.toRadians(longitude)
        val lonOrigin = Math.toRadians(centralMeridian)

        // 타원체 파라미터 (Bessel 1841)
        val a = 6377397.155 // 장반경
        val f = 1.0 / 299.1528128 // 편평률
        val e2 = 2 * f - f * f // 이심률의 제곱

        // TM 투영 계산 (단순화된 버전)
        val n = a / sqrt(1 - e2 * sin(lat).pow(2))
        val t = tan(lat).pow(2)
        val c = (e2 / (1 - e2)) * cos(lat).pow(2)
        val lonDelta = lon - lonOrigin

        // X 좌표 (Easting)
        val x =
            scaleFactor * n *
                (
                    lonDelta * cos(lat) +
                        lonDelta.pow(3) * cos(lat).pow(3) * (1 - t + c) / 6
                )

        // Y 좌표 (Northing)
        val m =
            a *
                (
                    (1 - e2 / 4 - 3 * e2.pow(2) / 64) * lat -
                        (3 * e2 / 8 + 3 * e2.pow(2) / 32) * sin(2 * lat) +
                        (15 * e2.pow(2) / 256) * sin(4 * lat)
                )

        val y =
            scaleFactor *
                (
                    m +
                        n * tan(lat) *
                        (
                            lonDelta.pow(2) * cos(lat).pow(2) / 2 +
                                lonDelta.pow(4) * cos(lat).pow(4) * (5 - t + 9 * c + 4 * c.pow(2)) / 24
                        )
                )

        val tmX = (x + falseEasting).toInt().toString()
        val tmY = (y + falseNorthing).toInt().toString()

        return Pair(tmX, tmY)
    }
}
