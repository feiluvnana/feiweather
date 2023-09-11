package com.example.feiweather.data
import kotlinx.serialization.Serializable

@Serializable
data class Condition(
    val code: Int? = null,
    val icon: String,
    val text: String
)