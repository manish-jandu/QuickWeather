package com.manishjandu.quickweather.data.models

data class ForecastDay(
    val astro: Astro,
    val date: String,
    val date_epoch: Int,
    val day: Day,
) {
    data class Astro(
        val moon_illumination: String,
        val moon_phase: String,
        val moonrise: String,
        val moonset: String,
        val sunrise: String,
        val sunset: String
    )

    data class Day(
        val avghumidity: Double,
        val avgtemp_c: Double,
        val avgtemp_f: Double,
        val avgvis_km: Double,
        val avgvis_miles: Double,
        val condition: Condition,
        val daily_chance_of_rain: String,
        val daily_chance_of_snow: String,
        val daily_will_it_rain: Int,
        val daily_will_it_snow: Int,
        val maxtemp_c: Double,
        val maxtemp_f: Double,
        val maxwind_kph: Double,
        val maxwind_mph: Double,
        val mintemp_c: Double,
        val mintemp_f: Double,
        val totalprecip_in: Double,
        val totalprecip_mm: Double,
        val uv: Double
    ) {
        data class Condition(
            val code: Int,
            val icon: String,
            val text: String
        )
    }
}


