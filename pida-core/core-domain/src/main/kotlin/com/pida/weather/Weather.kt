package com.pida.weather

import java.time.LocalDateTime

/**
 * 날씨 정보
 */
data class Weather(
    val location: WeatherLocation,
    val precipitationType: PrecipitationType,
    val precipitationProbability: Int,
    val precipitation: Double,
    val skyCondition: SkyCondition,
    val temperature: Double,
    val humidity: Int,
    val forecastDateTime: LocalDateTime,
) {
    /**
     * 비가 오는지 여부
     */
    fun isRaining(): Boolean = precipitationType.isRain()

    /**
     * 비 예보가 있는지 여부
     */
    fun hasRainForecast(threshold: Int = 30): Boolean = precipitationProbability >= threshold

    /**
     * 강수량이 특정 값 이상인지 여부
     */
    fun isPrecipitationAbove(threshold: Double): Boolean = precipitation >= threshold
}

/**
 * 강수 형태
 */
enum class PrecipitationType(
    val code: String,
    val description: String,
) {
    NONE("0", "없음"),
    RAIN("1", "비"),
    RAIN_SNOW("2", "비/눈"),
    SNOW("3", "눈"),
    SHOWER("4", "소나기"),
    RAIN_DROP("5", "빗방울"),
    RAIN_SNOW_DROP("6", "빗방울눈날림"),
    SNOW_DROP("7", "눈날림"),
    ;

    fun isRain(): Boolean = this in listOf(RAIN, RAIN_SNOW, SHOWER, RAIN_DROP)

    companion object {
        fun fromCode(code: String): PrecipitationType = entries.find { it.code == code } ?: NONE
    }
}

/**
 * 하늘 상태
 */
enum class SkyCondition(
    val code: String,
    val description: String,
) {
    CLEAR("1", "맑음"),
    PARTLY_CLOUDY("3", "구름많음"),
    CLOUDY("4", "흐림"),
    ;

    companion object {
        fun fromCode(code: String): SkyCondition = entries.find { it.code == code } ?: CLEAR
    }
}
