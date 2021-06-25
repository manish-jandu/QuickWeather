package com.manishjandu.quickweather.data.local

 import android.content.Context
 import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
 import com.manishjandu.quickweather.R

@Database(entities = [LocationInLongLat::class], version = 1, exportSchema = false)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao

    companion object {

        @Volatile
        private var instance: LocationDatabase? = null

        fun getDatabase(context: Context): LocationDatabase {
            synchronized(this) {
                if (instance == null) {
                    instance = createInstance(context)
                }
                return instance!!
            }
        }

        private fun createInstance(context: Context): LocationDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                LocationDatabase::class.java,
                "location_database"
            )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Thread(Runnable {
                            prePopulateDatabase(
                                context,
                                getDatabase(context)
                            )
                        }).start()
                    }
                })
                .build()
        }

        private fun prePopulateDatabase(context: Context, database: LocationDatabase) {
            val location = LocationInLongLat(context.getString(R.string.firstTimeLocation))
            database.locationDao().setFirstTimeLocation(location)

        }
    }
}