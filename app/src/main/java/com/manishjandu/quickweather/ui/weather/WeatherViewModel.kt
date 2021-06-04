package com.manishjandu.quickweather.ui.weather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.manishjandu.WeatherApplication
import com.manishjandu.quickweather.data.WeatherRepository
import com.manishjandu.quickweather.data.models.WeatherData
import com.manishjandu.quickweather.utils.slideToSearchScreenSendSignal
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


private const val TAG = "WeatherViewModel"

class WeatherViewModel(app: Application) : AndroidViewModel(app) {


    private var _weatherData = MutableLiveData<WeatherData>()
    val weatherData: LiveData<WeatherData> = _weatherData

    private val weatherEventChannel = Channel<WeatherEvent>()
    val weatherEvent = weatherEventChannel.receiveAsFlow()

    val mFusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(app.applicationContext)


    val repo = WeatherRepository()

    fun hasInternetAndLocationEnabled() = viewModelScope.launch {
        val internetEnabled = checkInternetEnabled()
        val locationEnabled = checkLocationEnabled()

        if (internetEnabled && locationEnabled) {
            weatherEventChannel.send(WeatherEvent.InternetAndLocationEnabled(true))
        } else if (!internetEnabled) {
            weatherEventChannel.send(WeatherEvent.InternetNotEnabledError)
        } else if (!locationEnabled) {
            weatherEventChannel.send(WeatherEvent.LocationNotEnabledError)
        }
    }

    private fun checkInternetEnabled(): Boolean {
        val connectivityManager =
            getApplication<WeatherApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    private fun checkLocationEnabled(): Boolean {
        val locationManager =
            getApplication<WeatherApplication>().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return gps_enabled
    }


    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        Log.i(TAG, "getLastLocation: Asking for location --------------------------------------")
        mFusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            Log.i(TAG, "getLastLocation: ${task.result}")
            viewModelScope.launch {
                var lastLocation = "Jaipur"
                if (task.isSuccessful && task.result != null) {
                    val mLatitude = task.result.latitude
                    val mLongitude = task.result.longitude
                    //_mLastLocation.value = "$mLatitude,$mLongitude"
                    lastLocation = "$mLatitude,$mLongitude"
                    weatherEventChannel.send(WeatherEvent.LastLocation(lastLocation))
                } else {
                    weatherEventChannel.send(WeatherEvent.ShowErrorMessage)
                }
            }
        }
    }

    fun getWeatherData(lastLocation: String) = viewModelScope.launch {
        val result = repo.getWeatherFromRemote(lastLocation)
        //val result = null
        if (result != null) {
            _weatherData.value = result
        } else {
            weatherEventChannel.send(WeatherEvent.ShowErrorMessage)
        }
    }

    fun slideToSearchScreen() = viewModelScope.launch{
        slideToSearchScreenSendSignal()
     }

    sealed class WeatherEvent {
        object ShowErrorMessage : WeatherEvent()
        object LocationNotEnabledError : WeatherEvent()
        object InternetNotEnabledError : WeatherEvent()
         data class InternetAndLocationEnabled(val bothEnabled: Boolean) : WeatherEvent()
        data class LastLocation(val lastLocation: String) : WeatherEvent()
    }
}