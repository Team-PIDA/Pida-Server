package com.pida.weather

import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tan

/**
 * 날씨 위치 정보
 */
data class WeatherLocation(
    val latitude: Double, // 위도
    val longitude: Double, // 경도
    val nx: Int, // 기상청 격자 X 좌표
    val ny: Int, // 기상청 격자 Y 좌표
) {
    companion object {
        /**
         * GPS 좌표(위도, 경도)를 기상청 격자 좌표(nx, ny)로 변환
         *
         * 기상청 좌표 변환 공식 참고
         * https://www.kma.go.kr/images/weather/lifenindustry/timeseries_XML.pdf
         */
        fun fromCoordinates(
            latitude: Double,
            longitude: Double,
        ): WeatherLocation {
            val (nx, ny) = convertToGrid(latitude, longitude)
            return WeatherLocation(latitude, longitude, nx, ny)
        }

        /**
         * GPS 좌표를 기상청 격자 좌표로 변환
         */
        private fun convertToGrid(
            lat: Double,
            lon: Double,
        ): Pair<Int, Int> {
            val earthRadius = 6371.00877 // 지구 반경(km)
            val gridSpacing = 5.0 // 격자 간격(km)
            val projectionLat1 = 30.0 // 투영 위도1(degree)
            val projectionLat2 = 60.0 // 투영 위도2(degree)
            val referenceLon = 126.0 // 기준점 경도(degree)
            val referenceLat = 38.0 // 기준점 위도(degree)
            val referenceX = 43.0 // 기준점 X좌표(GRID)
            val referenceY = 136.0 // 기준점 Y좌표(GRID)

            val degToRad = Math.PI / 180.0
            val re = earthRadius / gridSpacing
            val slat1 = projectionLat1 * degToRad
            val slat2 = projectionLat2 * degToRad
            val olon = referenceLon * degToRad
            val olat = referenceLat * degToRad

            var sn = tan(Math.PI * 0.25 + slat2 * 0.5) / tan(Math.PI * 0.25 + slat1 * 0.5)
            sn = ln(cos(slat1) / cos(slat2)) / ln(sn)
            var sf = tan(Math.PI * 0.25 + slat1 * 0.5)
            sf = sf.pow(sn) * cos(slat1) / sn
            var ro = tan(Math.PI * 0.25 + olat * 0.5)
            ro = re * sf / ro.pow(sn)

            var ra = tan(Math.PI * 0.25 + lat * degToRad * 0.5)
            ra = re * sf / ra.pow(sn)
            var theta = lon * degToRad - olon
            if (theta > Math.PI) theta -= 2.0 * Math.PI
            if (theta < -Math.PI) theta += 2.0 * Math.PI
            theta *= sn

            val x = (ra * sin(theta) + referenceX + 0.5).toInt()
            val y = (ro - ra * cos(theta) + referenceY + 0.5).toInt()

            return Pair(x, y)
        }
    }
}

/**
 * 주요 도시 좌표 상수
 */
object KoreanCities {
    val SEOUL = WeatherLocation.fromCoordinates(37.5665, 126.9780)
    val BUSAN = WeatherLocation.fromCoordinates(35.1796, 129.0756)
    val INCHEON = WeatherLocation.fromCoordinates(37.4563, 126.7052)
    val DAEGU = WeatherLocation.fromCoordinates(35.8714, 128.6014)
    val DAEJEON = WeatherLocation.fromCoordinates(36.3504, 127.3845)
    val GWANGJU = WeatherLocation.fromCoordinates(35.1595, 126.8526)
    val ULSAN = WeatherLocation.fromCoordinates(35.5384, 129.3114)
    val SEJONG = WeatherLocation.fromCoordinates(36.4800, 127.2890)
}
