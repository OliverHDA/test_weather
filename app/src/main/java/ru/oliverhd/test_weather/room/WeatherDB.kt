package ru.oliverhd.test_weather.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WeatherEntity::class], version = 1, exportSchema = false)
abstract class WeatherDB : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}