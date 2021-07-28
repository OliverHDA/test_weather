package ru.oliverhd.test_weather.model

import com.google.gson.annotations.SerializedName

data class DailyWeather(
    @field:SerializedName("dt") val dt: Long,
    @field:SerializedName("temp") val temp: Temp,
    @field:SerializedName("weather") val weather: List<Weather>
)