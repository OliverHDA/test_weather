package ru.oliverhd.test_weather.app

import android.app.Application
import androidx.room.Room
import ru.oliverhd.test_weather.room.WeatherDB
import ru.oliverhd.test_weather.room.WeatherDao

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    companion object {

        private var appInstance: App? = null
        private var db: WeatherDB? = null
        private const val DB_NAME = "Weather.db"

        fun getWeatherDao(): WeatherDao {
            if (db == null) {
                synchronized(WeatherDB::class.java) {
                    if (db == null) {
                        if (appInstance == null) throw IllegalStateException("Application is null while creating DataBase")
                        db = Room.databaseBuilder(
                            appInstance!!.applicationContext,
                            WeatherDB::class.java,
                            DB_NAME
                        )
                            .allowMainThreadQueries()
                            .build()
                    }
                }
            }
            return db!!.weatherDao()
        }
    }
}