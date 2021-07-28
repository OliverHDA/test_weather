package ru.oliverhd.test_weather.model

import com.google.gson.annotations.SerializedName

data class CurrentWeather(
    @field:SerializedName("dt") val dt: Long,
    @field:SerializedName("temp") val temp: Double,
    @field:SerializedName("weather") val weather: List<Weather>
)