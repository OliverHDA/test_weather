package ru.oliverhd.test_weather.room

import com.google.gson.Gson
import ru.oliverhd.test_weather.model.WeatherFromDao
import ru.oliverhd.test_weather.model.WeatherResponseData

fun convertWeatherEntityToWeatherData(entity: WeatherEntity): WeatherResponseData {
    val weatherFromDao: WeatherFromDao = convertWeatherEntityToWeatherFromDao(entity)
    return WeatherResponseData(
        entity.lat,
        entity.lon,
        weatherFromDao.current,
        weatherFromDao.daily
    )
}

fun convertWeatherEntityToWeatherFromDao(entity: WeatherEntity): WeatherFromDao {
    return Gson().fromJson(entity.json, WeatherFromDao::class.java)
}

fun convertWeatherToWeatherEntity(lat: Double, lon: Double, json: String): WeatherEntity {
    return WeatherEntity(
        0,
        lat,
        lon,
        System.currentTimeMillis(),
        json
    )
}