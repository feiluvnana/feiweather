package com.example.feiweather.clients

import com.example.feiweather.data.ForecastWeather
import com.example.feiweather.data.Location
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

object Constant {
    const val API_KEY: String = "27aa1b69fcd14156816144333233008"
    private const val BASE_URL: String = "http://api.weatherapi.com/v1"
    const val FORECAST: String = "$BASE_URL/forecast.json"
    const val SEARCH: String = "$BASE_URL/search.json"
}

interface WeatherAPI {
    suspend fun getForecastWeather(days: Int, q: String): ForecastWeather?
    suspend fun getAutoComplete(q: String): List<Location>?

    companion object {
        fun create(): WeatherAPI {
            return WeatherAPIImpl(
                client = HttpClient(CIO) {
                    expectSuccess = true
                }
            )
        }
    }
}

class WeatherAPIImpl(private val client: HttpClient) : WeatherAPI {
    override suspend fun getForecastWeather(days: Int, q: String): ForecastWeather? {
        try {
            val httpResponse: HttpResponse = client.get(Constant.FORECAST) {
                parameter("key", Constant.API_KEY)
                parameter("q", q)
                parameter("lang", "vi")
                parameter("days", days)
            }
            return Json.decodeFromString<ForecastWeather>(httpResponse.body())
        } catch (e: Throwable) {
            return null
        }
    }

    override suspend fun getAutoComplete(q: String): List<Location>? {
        try {
            if (q.isEmpty()) return listOf<Location>()
            val httpResponse: HttpResponse = client.get(Constant.SEARCH) {
                parameter("key", Constant.API_KEY)
                parameter("q", q)
                parameter("lang", "vi")
            }
            return Json.decodeFromString(httpResponse.body())
        } catch (e: Throwable) {
            return null
        }
    }
}