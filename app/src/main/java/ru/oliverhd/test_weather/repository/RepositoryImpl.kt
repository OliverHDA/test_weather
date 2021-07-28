package ru.oliverhd.test_weather.repository

import retrofit2.Callback
import ru.oliverhd.test_weather.BuildConfig
import ru.oliverhd.test_weather.model.WeatherResponseData
import ru.oliverhd.test_weather.room.WeatherDao
import ru.oliverhd.test_weather.room.WeatherEntity
import ru.oliverhd.test_weather.room.convertWeatherEntityToWeatherData
import java.text.DecimalFormat

const val ONE_HOUR: Long = 3600000

class RepositoryImpl(private val server: WeatherRetrofitImpl, private val local: WeatherDao) :
    Repository {

    override fun getWeatherFromServer(
        lat: Double,
        lon: Double,
        callback: Callback<WeatherResponseData>
    ) {
        val apiKey: String = BuildConfig.WEATHER_API_KEY
        server.getRetrofitImpl()
            .getDailyWeather(lat, lon, "minutely,hourly,alerts", "metric", apiKey, "ru")
            .enqueue(callback)
    }

    override fun getWeatherFromLocal(
        lat: Double,
        lon: Double
    ): WeatherResponseData? {
        val df = DecimalFormat("##.####")
        df.format(lat)
        val entity: WeatherEntity? =
            local.getWeatherFromRoom(df.format(lat).toDouble(), df.format(lon).toDouble())
        entity?.let {
            if (System.currentTimeMillis() - entity.currentTime > ONE_HOUR) {
                return null
            } else {
                return convertWeatherEntityToWeatherData(it)
            }
        }
        return null
    }

    override fun saveWeather(entity: WeatherEntity) {
        local.insertWeather(entity)
    }
}