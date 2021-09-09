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
import com.manishjandu.quickweather.utils.Constants
import com.manishjandu.quickweather.utils.LocationResult
import com.manishjandu.quickweather.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject


private const val TAG = "WeatherViewModel"

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repo: WeatherRepository,
    @ApplicationContext context: Context
) : ViewModel() {
    var isInternetAvailable: Boolean? = null

    private var _weatherData = MutableLiveData<NetworkResult<WeatherData>>()
    val weatherData: LiveData<NetworkResult<WeatherData>> = _weatherData

    private var _location = MutableLiveData<LocationResult>()
    val location: LiveData<LocationResult> = _location

    private var _multipleLocations = MutableLiveData<NetworkResult<List<LocationsItem>>>()
    val multipleLocations: LiveData<NetworkResult<List<LocationsItem>>> = _multipleLocations

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
                _location.postValue(LocationResult.Error(Constants.CANNOT_GET_LAST_LOCATION))
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
            response.code() == 402 -> {
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

    fun setNewLocation(newLocation: String) {
        _location.postValue(LocationResult.Success(newLocation))
    }

    fun getLocations(query: String) = viewModelScope.launch {
        _multipleLocations.postValue(NetworkResult.Loading())
        if (checkInternetConnection()) {
            try {
                val result = repo.getLocationsNames(query)
                _multipleLocations.postValue(handleMultipleLocationsResponse(result))
            } catch (e: Exception) {
                _multipleLocations.postValue(NetworkResult.Error(e.message.toString()))
            }
        } else {
            _multipleLocations.postValue(NetworkResult.Error("No Internet Connection."))
        }
    }

    private fun handleMultipleLocationsResponse(response: Response<List<LocationsItem>>): NetworkResult<List<LocationsItem>> {
        return when {
            response.message().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
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

    private fun checkInternetConnection(): Boolean {
        return if (isInternetAvailable != null) {
            isInternetAvailable == true
        } else {
            false
        }
    }

}