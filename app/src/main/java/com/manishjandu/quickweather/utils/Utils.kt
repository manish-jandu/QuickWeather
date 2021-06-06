package com.manishjandu.quickweather.utils

import androidx.fragment.app.viewModels
import com.google.android.gms.location.LocationServices
import com.manishjandu.quickweather.ui.weather.WeatherViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import java.text.SimpleDateFormat
import java.util.*

fun parseDateLesserThenVersionO(date: String): Date {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
    return dateFormat.parse(date)
}

fun getTimeDifference(localTime: String, lastUpdated: String): Int {
    val local = parseDateLesserThenVersionO(localTime)
    val updated = parseDateLesserThenVersionO(lastUpdated)
    return local.compareTo(updated)
}

private val UtilsEventChannel = Channel<UtilsEvent>()
val utilEvent = UtilsEventChannel.receiveAsFlow()

suspend fun slideToSearchScreenSendSignal() {
    UtilsEventChannel.send(UtilsEvent.SlideToSearchScreen)
}
suspend fun slideToWeatherScreenSendSignal() {
    UtilsEventChannel.send(UtilsEvent.SlideToWeatherScreen)
}

suspend fun setNewWeatherLocation(location: String) {
    UtilsEventChannel.send(UtilsEvent.NewWeatherLocation(location))
}

sealed class UtilsEvent {
    object SlideToSearchScreen : UtilsEvent()
    object SlideToWeatherScreen : UtilsEvent()
    data class NewWeatherLocation(val newLocation: String) : UtilsEvent()
}
