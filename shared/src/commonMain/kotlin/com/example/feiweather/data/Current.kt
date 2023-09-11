package com.example.feiweather.data
import kotlinx.serialization.Serializable

@Serializable
data class Current(
    val condition: Condition,
    val feelslike_c: Double,
    val feelslike_f: Double,
    val humidity: Int,
    val last_updated: String,
    val pressure_mb: Double,
    val temp_c: Double,
    val temp_f: Double,
    val uv: Double,
    val vis_km: Double,
    val wind_degree: Int,
    val wind_dir: String,
    val wind_kph: Double,
)