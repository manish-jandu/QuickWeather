package com.manishjandu.quickweather

import com.manishjandu.quickweather.data.WeatherRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    val repo = WeatherRepository()

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test()
    fun repo_test() = runBlocking{
        val result = repo.getWeatherFromRemote("London")
    }
}