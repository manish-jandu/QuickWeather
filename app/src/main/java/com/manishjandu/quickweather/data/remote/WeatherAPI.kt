package com.manishjandu.quickweather.data.remote

import com.manishjandu.quickweather.BuildConfig
import com.manishjandu.quickweather.data.models.WeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface WeatherAPI {
    companion object {
        const val BASE_URL = "http://api.weatherapi.com/v1/"
        const val CLIENT_ID = BuildConfig.WEATHER_API_ACCESS_KEY
    }

    @Headers("key:$CLIENT_ID")
    @GET("forecast.json")
    suspend fun getWeather(
        @Query("q")
        query: String,
        @Query("days")
        days: Int = 3
    ):Response<WeatherData>
}