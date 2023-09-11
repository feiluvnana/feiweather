package com.example.feiweather.data
import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val id: Int? = null,
    val country: String,
    val lat: Double,
    val localtime: String? = null,
    val localtime_epoch: Int? = null,
    val lon: Double,
    val name: String,
    val region: String,
    val tz_id: String? = null,
    val url: String? = null
)