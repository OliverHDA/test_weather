package ru.oliverhd.test_weather.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["lat", "lon"], unique = true)])
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "lat")
    val lat: Double = 59.9342802,
    @ColumnInfo(name = "lon")
    val lon: Double = 30.3350986,
    val currentTime: Long = System.currentTimeMillis(),
    val json: String = ""
)