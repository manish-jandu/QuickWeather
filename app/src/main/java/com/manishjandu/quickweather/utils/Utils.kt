package com.manishjandu.quickweather.utils


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
