package ru.oliverhd.test_weather.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    @GET("data/2.5/onecall")
    fun getDailyWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String,
        @Query("units") units: String,
        @Query("appid") appid: String,
        @Query("lang") lang: String
    ): Call<WeatherResponseData>
}