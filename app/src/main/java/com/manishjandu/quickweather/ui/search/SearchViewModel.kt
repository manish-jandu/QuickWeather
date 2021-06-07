package com.manishjandu.quickweather.ui.search

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.manishjandu.quickweather.data.WeatherRepository
import com.manishjandu.quickweather.data.local.LocationDatabase
import com.manishjandu.quickweather.data.models.LocationsItem
  import com.manishjandu.quickweather.utils.setCurrentWeatherLocation
import com.manishjandu.quickweather.utils.setNewWeatherLocation
import com.manishjandu.quickweather.utils.slideToWeatherScreenSendSignal
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val TAG = "SearchViewModel"

class SearchViewModel (app: Application) : AndroidViewModel(app) {
    private var _locations = MutableLiveData<List<LocationsItem>>()
    val locations: LiveData<List<LocationsItem>> = _locations

    private val searchEventChannel = Channel<SearchEvent>()
    val searchEvent = searchEventChannel.receiveAsFlow()

    private val dao = LocationDatabase.getDatabase(app.applicationContext).locationDao()

    private val repo = WeatherRepository(dao)

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

    fun getCurrentLocation() =viewModelScope.launch{
        slideToWeatherScreenSendSignal()
        setCurrentWeatherLocation()
    }

    sealed class SearchEvent {
        object LocationNotFound : SearchEvent()
    }
}