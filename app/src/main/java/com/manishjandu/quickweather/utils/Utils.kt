package com.manishjandu.quickweather.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
fun parseDateGreaterThenVersionO(date: String): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val date = LocalDateTime.parse(date, formatter)
    return date
}

fun parseDateLesserThenVersionO(date: String): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
    return sdf.format(date)
}


//@RequiresApi(Build.VERSION_CODES.O)
//fun getDifference(localTime: String, LastUpdated: String): Int {
//    val local = parseDateGreaterThenVersionO(localTime)
//    val updated = parseDateGreaterThenVersionO(LastUpdated)
//    return local.minute.minus(updated.minute)
//}

fun getLocalTime() {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
          parseDateGreaterThenVersionO(Date().toString())
    }else{
        parseDateLesserThenVersionO(Date().toString())
    }
}
