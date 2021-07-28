package ru.oliverhd.test_weather.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeather(entity: WeatherEntity)

    @Query("SELECT * FROM WeatherEntity WHERE lat = :lat AND lon = :lon")
    fun getWeatherFromRoom(lat: Double, lon: Double): WeatherEntity?
}