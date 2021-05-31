package com.manishjandu.quickweather.data

import com.manishjandu.quickweather.data.models.WeatherData
import com.manishjandu.quickweather.data.remote.WeatherClient

private const val TAG = "WeatherRepository"

class WeatherRepository {
    private val api = WeatherClient.api

    suspend fun getWeatherFromRemote(lastLocation: String): WeatherData? {
        val result = api.getWeather(lastLocation)
        if (result.isSuccessful && result.code() == 200) {
            return result.body()
        }
        return null
    }
}