package ru.oliverhd.test_weather.model

import com.google.gson.annotations.SerializedName

data class WeatherFromDao(
    @field:SerializedName("current") val current: CurrentWeather,
    @field:SerializedName("daily") val daily: List<DailyWeather>
)