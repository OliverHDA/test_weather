package ru.oliverhd.test_weather.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.oliverhd.test_weather.BuildConfig
import ru.oliverhd.test_weather.app.App
import ru.oliverhd.test_weather.app.AppData
import ru.oliverhd.test_weather.model.WeatherResponseData
import ru.oliverhd.test_weather.repository.RepositoryImpl
import ru.oliverhd.test_weather.retrofit.WeatherRetrofitImpl
import ru.oliverhd.test_weather.room.convertWeatherToWeatherEntity

class MainViewModel(
    private val liveDataToObserve: MutableLiveData<AppData> = MutableLiveData(),
    private val repositoryImpl: RepositoryImpl = RepositoryImpl(
        WeatherRetrofitImpl(),
        App.getWeatherDao()
    )
) : ViewModel() {
    fun getData() = liveDataToObserve

    fun getWeather(lat: Double, lon: Double): LiveData<AppData> {
        sendRequest(lat, lon)
        return liveDataToObserve
    }

    private fun sendRequest(lat: Double, lon: Double) {
        liveDataToObserve.value = AppData.Loading(null)
        val apiKey: String = BuildConfig.WEATHER_API_KEY

        repositoryImpl.getWeatherFromLocal(lat, lon)?.let {
            liveDataToObserve.value = AppData.Success(it)
        } ?: run {
            if (apiKey.isBlank()) {
                AppData.Error(Throwable("You need API key"))
            } else {
                repositoryImpl.getWeatherFromServer(lat, lon, callBack)
            }
        }
    }

    private val callBack = object :
        Callback<WeatherResponseData> {
        override fun onResponse(
            call: Call<WeatherResponseData>,
            response: Response<WeatherResponseData>
        ) {
            if (response.isSuccessful && response.body() != null) {

                repositoryImpl.saveWeather(
                    convertWeatherToWeatherEntity(
                        response.body()!!.lat,
                        response.body()!!.lon,
                        JSONObject(Gson().toJson(response.body())).toString()
                    )
                )

                liveDataToObserve.value =
                    AppData.Success(response.body()!!)
            } else {
                val message = response.message()
                if (message.isNullOrEmpty()) {
                    liveDataToObserve.value =
                        AppData.Error(Throwable("Unidentified error"))
                } else {
                    liveDataToObserve.value =
                        AppData.Error(Throwable(message))
                }
            }
        }

        override fun onFailure(call: Call<WeatherResponseData>, t: Throwable) {
            liveDataToObserve.value = AppData.Error(t)
        }
    }
}