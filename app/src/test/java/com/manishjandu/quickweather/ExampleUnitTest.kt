package com.manishjandu.quickweather

import android.util.Log
import com.manishjandu.quickweather.data.WeatherRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
private const val TAG = "ExampleUnitTest"

class ExampleUnitTest {
    val repo = WeatherRepository()

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test()
    fun repo_test() = runBlocking {
        val result = repo.getWeatherFromRemote("London")
        assertNotNull(result)
    }

    @Test()
    fun apiTest() = runBlocking {
        val result = repo.getLocationsNames("london")
        assertNotNull(result)
    }
}