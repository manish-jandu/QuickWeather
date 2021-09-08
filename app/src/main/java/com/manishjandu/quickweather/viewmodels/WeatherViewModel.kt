package com.manishjandu.quickweather.viewmodels

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
import com.manishjandu.quickweather.data.models.LocationsItem
import com.manishjandu.quickweather.data.models.WeatherData
import com.manishjandu.quickweather.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject


private const val TAG = "WeatherViewModel"

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repo: WeatherRepository,
    @ApplicationContext context: Context
) : ViewModel() {

    private var _weatherData = MutableLiveData<NetworkResult<WeatherData>>()
    val weatherData: LiveData<NetworkResult<WeatherData>> = _weatherData

    private var _location = MutableLiveData<LocationResult>()
    val location: LiveData<LocationResult> = _location

    private var _locations = MutableLiveData<List<LocationsItem>>()
    val locations: LiveData<List<LocationsItem>> = _locations

    private val searchEventChannel = Channel<SearchEvent>()
    val searchEvent = searchEventChannel.receiveAsFlow()

    private val mFusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)


    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        Log.i(TAG, "getLastLocation: Asking for location --------------------------------------")
        mFusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            Log.i(TAG, "getLastLocation: ${task.result}")
            if (task.isSuccessful && task.result != null) {
                val mLatitude = task.result.latitude
                val mLongitude = task.result.longitude
                val lastLocation = "$mLatitude,$mLongitude"
                _location.postValue(LocationResult.Success(lastLocation))
            } else {
                _location.postValue(LocationResult.Error("something went wrong"))
            }
        }
    }

    fun getLocationDataFromDatabase() = viewModelScope.launch(Dispatchers.IO) {
        val result = repo.getLocationDataLocally()
        _location.postValue(LocationResult.Success(result.location))
    }

    fun setLocationDataInRoom(newLocation: String) = viewModelScope.launch {
        repo.setLocationDataLocally(newLocation)
    }

    fun getWeatherData(lastLocation: String) = viewModelScope.launch(Dispatchers.IO) {
        _weatherData.postValue(NetworkResult.Loading())
        try {
            val result = repo.getWeatherFromRemote(lastLocation)
            _weatherData.postValue(handleWeatherDataResponse(result))
        } catch (e: Exception) {
            _weatherData.postValue(NetworkResult.Error(e.message.toString()))
        }
    }

    private fun handleWeatherDataResponse(response: Response<WeatherData>): NetworkResult<WeatherData> {
        return when {
            response.message().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.code() == 402-> {
                NetworkResult.Error("Api Limit")
            }
            response.isSuccessful -> {
                val result = response.body()!!
                NetworkResult.Success(result)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    fun getLocations(query: String) = viewModelScope.launch {
        val result = repo.getLocationsNames(query)
        if (result != null && result.isNotEmpty()) {
            _locations.value = result
        } else {
            searchEventChannel.send(SearchEvent.LocationNotFound)
        }
    }

    fun setNewLocationAndSlide(newLocation: String) = viewModelScope.launch {
        slideToWeatherScreenSendSignal()
        setNewWeatherLocation(newLocation)
    }

    fun getCurrentLocation() = viewModelScope.launch {
        slideToWeatherScreenSendSignal()
        setCurrentWeatherLocation()
    }

    sealed class SearchEvent {
        object LocationNotFound : SearchEvent()
    }
}