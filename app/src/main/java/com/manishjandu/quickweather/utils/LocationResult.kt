package com.manishjandu.quickweather.utils

sealed class LocationResult {
    class Error(val message:String):LocationResult()
    class Success(  val location:String):LocationResult()
}