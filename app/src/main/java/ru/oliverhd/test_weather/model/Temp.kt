package ru.oliverhd.test_weather.model

import com.google.gson.annotations.SerializedName

data class Temp(
    @field:SerializedName("day") val day: Float
)