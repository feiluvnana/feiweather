package com.example.feiweather.data
import kotlinx.serialization.Serializable

@Serializable
data class ForecastWeather(
    val current: Current,
    val forecast: Forecast,
    val location: Location
) {
    val currentWeather : CurrentWeather
        get() = CurrentWeather(current = current, location = location)
}