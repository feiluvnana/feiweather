package com.example.feiweather.data
import kotlinx.serialization.Serializable

@Serializable
data class Hour(
    val condition: Condition,
    val chance_of_rain: Int,
    val precip_mm: Double,
    val temp_c: Double,
    val temp_f: Double,
    val time: String,
)