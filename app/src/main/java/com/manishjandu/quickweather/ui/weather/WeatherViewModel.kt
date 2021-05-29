package com.manishjandu.quickweather.ui.weather

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.manishjandu.quickweather.data.models.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "WeatherViewModel"

class WeatherViewModel : ViewModel() {
    var _mLastLocation = MutableLiveData<String>()
    val mLastLocation: LiveData<String> = _mLastLocation
    var _weatherData = MutableLiveData<WeatherData>()
    val weatherData: LiveData<WeatherData> = _weatherData

    @SuppressLint("MissingPermission")
    fun getLastLocation(mFusedLocationProviderClient: FusedLocationProviderClient) {
        mFusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                val mLatitude = task.result.latitude
                val mLongitude = task.result.longitude
                _mLastLocation.value = "$mLatitude,$mLongitude"
            } else {
                //Todo:snackbar, something went wrong location not detected
                Log.i(TAG, "cannnot get location")
                _mLastLocation.value = "51.5074,0.1278"
            }
        }
    }

    fun getWeatheData(lastLocation: String) = viewModelScope.launch(Dispatchers.IO) {

    }


}