package ru.oliverhd.test_weather.model

import com.google.gson.annotations.SerializedName

data class WeatherResponseData(
    @field:SerializedName("lat") val lat: Double,
    @field:SerializedName("lon") val lon: Double,
    @field:SerializedName("current") val current: CurrentWeather,
    @field:SerializedName("daily") val daily: List<DailyWeather>
)