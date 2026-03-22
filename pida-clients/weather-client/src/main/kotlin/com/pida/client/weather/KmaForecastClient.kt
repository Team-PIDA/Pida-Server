package com.pida.client.weather

import java.time.LocalDateTime

interface KmaForecastClient {
    fun getVilageForecast(
        baseDate: String,
        baseTime: String,
        nx: Int,
        ny: Int,
        numOfRows: Int = 1000,
    ): KmaWeatherResponse

    fun getLatestBaseTime(currentTime: LocalDateTime = LocalDateTime.now()): Pair<String, String>
}
