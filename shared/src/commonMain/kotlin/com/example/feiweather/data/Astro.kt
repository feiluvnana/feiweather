package com.example.feiweather.data
import kotlinx.serialization.Serializable

@Serializable
data class Astro(
    val moon_phase: String,
    val moonrise: String,
    val moonset: String,
    val sunrise: String,
    val sunset: String
)