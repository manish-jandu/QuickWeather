package com.manishjandu.quickweather.utils


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



suspend fun setCurrentWeatherLocation() {
    UtilsEventChannel.send(UtilsEvent.CurrentWeatherLocation)
}

sealed class UtilsEvent {
    object SlideToSearchScreen : UtilsEvent()
    object SlideToWeatherScreen : UtilsEvent()
    object CurrentWeatherLocation : UtilsEvent()
    data class NewWeatherLocation(val newLocation: String) : UtilsEvent()
}
