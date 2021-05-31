package com.manishjandu.quickweather.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

//@RequiresApi(Build.VERSION_CODES.O)
//fun parseDateGreaterThenVersionO(date: String): LocalDateTime {
//    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
//    return LocalDateTime.parse(date, formatter)
//}

fun parseDateLesserThenVersionO(date: String): Date {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
    return dateFormat.parse(date)
}

//fun getDifference(localTime: String, lastUpdated: String): Int {
//    return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
//        val local = parseDateGreaterThenVersionO(localTime)
//        val updated = parseDateGreaterThenVersionO(lastUpdated)
//        local.minute.minus(updated.minute)
//    } else {
//        val local = parseDateLesserThenVersionO(localTime)
//        val updated = parseDateLesserThenVersionO(lastUpdated)
//        val diff = local.compareTo(updated)
//        0
//    }
//}

fun getTimeDifference(localTime: String, lastUpdated: String) :Int{
    val local = parseDateLesserThenVersionO(localTime)
    val updated = parseDateLesserThenVersionO(lastUpdated)
    return local.compareTo(updated)
}
