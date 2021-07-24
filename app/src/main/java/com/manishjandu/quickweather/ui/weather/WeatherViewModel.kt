package com.manishjandu.quickweather.ui.weather

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.manishjandu.quickweather.data.WeatherRepository
import com.manishjandu.quickweather.data.models.WeatherData
import com.manishjandu.quickweather.utils.slideToSearchScreenSendSignal
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "WeatherViewModel"

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repo: WeatherRepository,
    @ApplicationContext context: Context
) : ViewModel() {


    private var _weatherData = MutableLiveData<WeatherData>()
    val weatherData: LiveData<WeatherData> = _weatherData

    private val weatherEventChannel = Channel<WeatherEvent>()
    val weatherEvent = weatherEventChannel.receiveAsFlow()

    private val mFusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        Log.i(TAG, "getLastLocation: Asking for location --------------------------------------")
        mFusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            Log.i(TAG, "getLastLocation: ${task.result}")
            viewModelScope.launch {
                if (task.isSuccessful && task.result != null) {
                    val mLatitude = task.result.latitude
                    val mLongitude = task.result.longitude
                    val lastLocation = "$mLatitude,$mLongitude"
                    weatherEventChannel.send(WeatherEvent.LastLocation(lastLocation))
                } else {
                    weatherEventChannel.send(WeatherEvent.ShowErrorMessage)
                }
            }
        }
    }

    fun getWeatherData(lastLocation: String) = viewModelScope.launch {
        val result = repo.getWeatherFromRemote(lastLocation)
        if (result != null) {
            _weatherData.postValue(result)
            weatherEventChannel.send(WeatherEvent.Refreshed)
        } else {
            weatherEventChannel.send(WeatherEvent.ShowErrorMessage)
        }
    }

    fun slideToSearchScreen() = viewModelScope.launch {
        slideToSearchScreenSendSignal()
    }

    fun setLocationDataInRoom(newLocation: String) = viewModelScope.launch {
        repo.setLocationDataLocally(newLocation)
    }

    fun getLocationDataFromRoom() = viewModelScope.launch {
        val result = repo.getLocationDataLocally()
        weatherEventChannel.send(WeatherEvent.LocaleLocation(result.location))
    }

    sealed class WeatherEvent {
        object ShowErrorMessage : WeatherEvent()
        data class LastLocation(val lastLocation: String) : WeatherEvent()
        data class LocaleLocation(val location: String) : WeatherEvent()
        object Refreshed:WeatherEvent()
    }
}