package com.manishjandu.quickweather.data


import com.manishjandu.quickweather.data.local.LocationDao
import com.manishjandu.quickweather.data.local.LocationInLongLat
import com.manishjandu.quickweather.data.remote.WeatherAPI
import javax.inject.Inject

private const val TAG = "WeatherRepository"

class WeatherRepository @Inject constructor(
    private val dao: LocationDao,
    private val api: WeatherAPI
) {

    suspend fun getWeatherFromRemote(lastLocation: String) = api.getWeather(lastLocation)


    suspend fun getLocationsNames(locationQuery: String)= api.getLocationNames(locationQuery)

    suspend fun setLocationDataLocally(newLocation: String) {
        val new = LocationInLongLat(newLocation)
        dao.setLocation(newLocationInLongLat = new)
    }

    suspend fun getLocationDataLocally(): LocationInLongLat {
        return dao.getLocation()
    }
}


