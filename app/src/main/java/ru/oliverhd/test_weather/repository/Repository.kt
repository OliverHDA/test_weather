package ru.oliverhd.test_weather.repository

import ru.oliverhd.test_weather.model.WeatherResponseData
import ru.oliverhd.test_weather.room.WeatherEntity

interface Repository {
    fun getWeatherFromServer(
        lat: Double,
        lon: Double,
        callback: retrofit2.Callback<WeatherResponseData>
    )

    fun getWeatherFromLocal(
        lat: Double,
        lon: Double
    ): WeatherResponseData?

    fun saveWeather(entity: WeatherEntity)
}