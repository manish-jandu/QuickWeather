package com.manishjandu.quickweather.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manishjandu.quickweather.data.WeatherRepository
import com.manishjandu.quickweather.data.models.LocationsItem
import com.manishjandu.quickweather.utils.setCurrentWeatherLocation
import com.manishjandu.quickweather.utils.setNewWeatherLocation
import com.manishjandu.quickweather.utils.slideToWeatherScreenSendSignal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SearchViewModel"

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: WeatherRepository
) : ViewModel() {
    private var _locations = MutableLiveData<List<LocationsItem>>()
    val locations: LiveData<List<LocationsItem>> = _locations

    private val searchEventChannel = Channel<SearchEvent>()
    val searchEvent = searchEventChannel.receiveAsFlow()

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