package com.manishjandu.quickweather

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.manishjandu.quickweather.data.WeatherRepository
import com.manishjandu.quickweather.data.local.LocationDao
import com.manishjandu.quickweather.data.local.LocationDatabase
import com.manishjandu.quickweather.data.local.LocationInLongLat
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private lateinit var locationDao: LocationDao
    private lateinit var repo: WeatherRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        locationDao = LocationDatabase.getDatabase(context).locationDao()
        repo = WeatherRepository(locationDao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
       // db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {
        val loc1 = "hello"
        val loc2 = "second"
        val loc3 = "third"

        runBlocking {
            repo.setLocationDataLocally(loc1)
            repo.setLocationDataLocally(loc2)
            repo.setLocationDataLocally(loc3)
            runBlocking {
                val getLoc = repo.getLocationDataLocally()
                assertThat(getLoc.location, equalTo(loc3))
            }
        }

    }
}