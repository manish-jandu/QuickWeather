package com.manishjandu.quickweather.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocationDao {

    @Query("SELECT * FROM last_location_table WHERE id = 1")
    suspend fun getLocation(): LocationInLongLat

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setLocation(newLocationInLongLat: LocationInLongLat)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setFirstTimeLocation(newLocationInLongLat: LocationInLongLat)
}