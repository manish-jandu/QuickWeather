package com.manishjandu.quickweather.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manishjandu.quickweather.data.WeatherRepository
import com.manishjandu.quickweather.data.models.LocationsItem
import com.manishjandu.quickweather.data.models.WeatherData
import com.manishjandu.quickweather.ui.weather.WeatherViewModel
import com.manishjandu.quickweather.utils.setNewWeatherLocation
import com.manishjandu.quickweather.utils.slideToWeatherScreenSendSignal
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val TAG = "SearchViewModel"

class SearchViewModel : ViewModel() {
    private var _locations = MutableLiveData<List<LocationsItem>>()
    val locations: LiveData<List<LocationsItem>> = _locations

    private val searchEventChannel = Channel<SearchEvent>()
    val searchEvent = searchEventChannel.receiveAsFlow()

    private val repo = WeatherRepository()

    fun getLocations(query: String) = viewModelScope.launch {
        val result = repo.getLocationsNames(query)
        if (result != null && result.isNotEmpty()) {
            _locations.value = result
        } else {
            searchEventChannel.send(SearchEvent.LocationNotFound)
        }
    }

    fun setNewLocationAndSlide(newLocation: String) = viewModelScope.launch{
        slideToWeatherScreenSendSignal()
        setNewWeatherLocation(newLocation)
    }

    sealed class SearchEvent {
        object LocationNotFound : SearchEvent()
    }
}