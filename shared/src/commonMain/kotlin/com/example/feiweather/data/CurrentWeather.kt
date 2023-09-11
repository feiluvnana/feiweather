package com.example.feiweather.data
import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeather(
    val current: Current,
    val location: Location
)