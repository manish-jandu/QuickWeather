package com.manishjandu.quickweather.data.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherData(
    @Json(name = "current")
    val current: Current,
    @Json(name = "forecast")
    val forecast: Forecast,
    @Json(name = "location")
    val location: Location
)