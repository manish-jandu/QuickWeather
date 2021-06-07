package com.manishjandu.quickweather.data.local

 import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "last_location_table")
data class LocationInLongLat(

    val location: String,

    @PrimaryKey(autoGenerate = false)
    val id: Int = 1
)
