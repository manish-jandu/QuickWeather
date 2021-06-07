package com.manishjandu.quickweather.data


 import com.manishjandu.quickweather.data.local.LocationDao
import com.manishjandu.quickweather.data.local.LocationInLongLat
 import com.manishjandu.quickweather.data.models.LocationsItem
import com.manishjandu.quickweather.data.models.WeatherData
import com.manishjandu.quickweather.data.remote.WeatherClient

private const val TAG = "WeatherRepository"

class WeatherRepository(private val dao: LocationDao) {
    private val api = WeatherClient.api


    suspend fun getWeatherFromRemote(lastLocation: String): WeatherData? {
        val result = api.getWeather(lastLocation)
        if (result.isSuccessful && result.code() == 200) {
            return result.body()
        }
        return null
    }

    suspend fun getLocationsNames(locationQuery: String): List<LocationsItem>? {
        val result = api.getLocationNames(locationQuery)
        if (result.isSuccessful && result.code() == 200) {
            return result.body()
        }
        return result.body()
    }

   suspend fun setLocationDataLocally(newLocation: String ) {
        val new = LocationInLongLat(newLocation)
        dao.setLocation(newLocationInLongLat = new)
    }

     suspend fun getLocationDataLocally( ): LocationInLongLat {
        return dao.getLocation()
    }
}


