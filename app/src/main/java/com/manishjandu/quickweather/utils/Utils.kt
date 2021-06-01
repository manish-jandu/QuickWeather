package com.manishjandu.quickweather.utils

import com.manishjandu.quickweather.ui.weather.WeatherViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import java.text.SimpleDateFormat
import java.util.*

fun parseDateLesserThenVersionO(date: String): Date {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
    return dateFormat.parse(date)
}

fun getTimeDifference(localTime: String, lastUpdated: String) :Int{
    val local = parseDateLesserThenVersionO(localTime)
    val updated = parseDateLesserThenVersionO(lastUpdated)
    return local.compareTo(updated)
}

private val UtilsEventChannel= Channel<UtilsEvent>()
val utilEvent = UtilsEventChannel.receiveAsFlow()

suspend fun slideToSearchScreenSendSignal(){
    UtilsEventChannel.send(UtilsEvent.SlideToSearchScreen)
}

sealed class UtilsEvent{
    object SlideToSearchScreen : UtilsEvent()
}
