package com.manishjandu.quickweather.data.models

import com.manishjandu.quickweather.data.models.Current
import com.manishjandu.quickweather.data.models.Forecast
import com.manishjandu.quickweather.data.models.Location

data class WeatherData(
    val current: Current,
    val location: Location,
    val forecast: Forecast
)