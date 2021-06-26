package com.manishjandu.quickweather.di

import android.content.Context
import com.manishjandu.quickweather.data.WeatherRepository
import com.manishjandu.quickweather.data.local.LocationDao
import com.manishjandu.quickweather.data.local.LocationDatabase
import com.manishjandu.quickweather.data.remote.WeatherAPI
import com.manishjandu.quickweather.data.remote.WeatherClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesDao(
        @ApplicationContext context:Context
    ): LocationDao {
        return LocationDatabase.getDatabase(context).locationDao()
    }

    @Provides
    fun providesApi(): WeatherAPI {
        return WeatherClient.api
    }

    @Provides
    fun providesWeatherRepository(
        dao: LocationDao,
        api: WeatherAPI
    ): WeatherRepository {
        return WeatherRepository(dao,api)
    }


}