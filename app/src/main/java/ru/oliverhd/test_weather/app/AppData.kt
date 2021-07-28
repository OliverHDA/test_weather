package ru.oliverhd.test_weather.app

import ru.oliverhd.test_weather.model.WeatherResponseData

sealed class AppData {
    data class Success(val serverResponseData: WeatherResponseData) : AppData()
    data class Error(val error: Throwable) : AppData()
    data class Loading(val progress: Int?) : AppData()
}