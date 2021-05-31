package com.manishjandu.quickweather.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherClient {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(WeatherAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api by lazy {
        retrofit.create(WeatherAPI::class.java)
    }
}