package com.manishjandu.quickweather.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manishjandu.quickweather.data.WeatherRepository
import com.manishjandu.quickweather.data.models.WeatherData
import com.manishjandu.quickweather.ui.weather.WeatherViewModel
import kotlinx.coroutines.launch

class SearchViewModel:ViewModel() {
    private var _weatherData = MutableLiveData<WeatherData>()
    val weatherData: LiveData<WeatherData> = _weatherData

    val repo = WeatherRepository()


    fun getWeatherData(location: String) = viewModelScope.launch {
        val result = repo.getWeatherFromRemote(location)
         if (result != null) {
            _weatherData.value = result
        } else {
           // weatherEventChannel.send(WeatherViewModel.WeatherEvent.ShowErrorMessage)
        }
    }
}