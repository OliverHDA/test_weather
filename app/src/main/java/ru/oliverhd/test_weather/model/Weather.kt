package ru.oliverhd.test_weather.model

import com.google.gson.annotations.SerializedName

data class Weather(
    @field:SerializedName("main") val main: String,
    @field:SerializedName("description") val description: String,
    @field:SerializedName("icon") val icon: String
)