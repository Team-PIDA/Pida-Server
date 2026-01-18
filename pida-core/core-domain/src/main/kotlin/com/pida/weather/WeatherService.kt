package com.pida.weather

/**
 * 날씨 서비스 인터페이스
 */
interface WeatherService {
    /**
     * 특정 위치의 날씨 정보 조회
     */
    fun getWeather(location: WeatherLocation): Weather

    /**
     * GPS 좌표로 날씨 정보 조회
     */
    fun getWeatherByCoordinates(
        latitude: Double,
        longitude: Double,
    ): Weather {
        val location = WeatherLocation.fromCoordinates(latitude, longitude)
        return getWeather(location)
    }

    /**
     * 비가 올 예정인지 확인
     */
    fun willItRain(
        location: WeatherLocation,
        probabilityThreshold: Int = 30,
    ): Boolean {
        val weather = getWeather(location)
        return weather.hasRainForecast(probabilityThreshold) || weather.isRaining()
    }
}
