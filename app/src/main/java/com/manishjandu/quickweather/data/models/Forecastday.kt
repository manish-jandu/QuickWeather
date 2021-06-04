package com.manishjandu.quickweather.data.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Forecastday(
    @Json(name = "astro")
    val astro: Astro,
    @Json(name = "date")
    val date: String,
    @Json(name = "date_epoch")
    val dateEpoch: Int,
    @Json(name = "day")
    val day: Day
) {
    @JsonClass(generateAdapter = true)
    data class Astro(
        @Json(name = "moon_illumination")
        val moonIllumination: String,
        @Json(name = "moon_phase")
        val moonPhase: String,
        @Json(name = "moonrise")
        val moonrise: String,
        @Json(name = "moonset")
        val moonset: String,
        @Json(name = "sunrise")
        val sunrise: String,
        @Json(name = "sunset")
        val sunset: String
    )

    @JsonClass(generateAdapter = true)
    data class Day(
        @Json(name = "avghumidity")
        val avghumidity: Double,
        @Json(name = "avgtemp_c")
        val avgtempC: Double,
        @Json(name = "avgtemp_f")
        val avgtempF: Double,
        @Json(name = "avgvis_km")
        val avgvisKm: Double,
        @Json(name = "avgvis_miles")
        val avgvisMiles: Double,
        @Json(name = "condition")
        val condition: Condition,
        @Json(name = "daily_chance_of_rain")
        val dailyChanceOfRain: String,
        @Json(name = "daily_chance_of_snow")
        val dailyChanceOfSnow: String,
        @Json(name = "daily_will_it_rain")
        val dailyWillItRain: Int,
        @Json(name = "daily_will_it_snow")
        val dailyWillItSnow: Int,
        @Json(name = "maxtemp_c")
        val maxtempC: Double,
        @Json(name = "maxtemp_f")
        val maxtempF: Double,
        @Json(name = "maxwind_kph")
        val maxwindKph: Double,
        @Json(name = "maxwind_mph")
        val maxwindMph: Double,
        @Json(name = "mintemp_c")
        val mintempC: Double,
        @Json(name = "mintemp_f")
        val mintempF: Double,
        @Json(name = "totalprecip_in")
        val totalprecipIn: Double,
        @Json(name = "totalprecip_mm")
        val totalprecipMm: Double,
        @Json(name = "uv")
        val uv: Double
    ) {
        @JsonClass(generateAdapter = true)
        data class Condition(
            @Json(name = "code")
            val code: Int,
            @Json(name = "icon")
            val icon: String,
            @Json(name = "text")
            val text: String
        )
    }
}