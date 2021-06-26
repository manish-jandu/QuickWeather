package com.manishjandu.quickweather.data.remote

import retrofit2.Retrofit
 import retrofit2.converter.moshi.MoshiConverterFactory

object WeatherClient {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(WeatherAPI.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    val api: WeatherAPI by lazy {
        retrofit.create(WeatherAPI::class.java)
    }

}