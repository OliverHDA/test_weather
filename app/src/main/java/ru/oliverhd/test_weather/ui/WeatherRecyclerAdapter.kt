package ru.oliverhd.test_weather.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import ru.oliverhd.test_weather.R
import ru.oliverhd.test_weather.model.DailyWeather
import java.text.SimpleDateFormat
import java.util.*

class WeatherRecyclerAdapter() : RecyclerView.Adapter<WeatherRecyclerAdapter.WeatherViewHolder>() {

    private var weatherData: List<DailyWeather> = listOf()

    fun setData(data: List<DailyWeather>) {
        weatherData = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.weather_recycler_item, parent, false)
        return WeatherViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.temperatureTextView.text = weatherData[position].temp.day.toInt().toString()
        val iconUrl =
            "https://openweathermap.org/img/wn/${weatherData[position].weather[0].icon}@2x.png"

        holder.iconImageView.load(iconUrl) {
            error(R.drawable.ic_load_error_vector)
            placeholder(R.drawable.ic_no_photo_vector)
        }

        val sdf = SimpleDateFormat("EEE, dd MMMM", Locale("ru"))
        val date = Date(weatherData[position].dt * 1000)
        holder.date.text = sdf.format(date)
    }

    override fun getItemCount(): Int {
        val i = weatherData.size
        return i
    }

    inner class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iconImageView: ImageView = itemView.findViewById(R.id.weatherRecyclerIcon)
        var temperatureTextView: TextView = itemView.findViewById(R.id.temperatureRecycler)
        var date: TextView = itemView.findViewById(R.id.date_text_view)
    }
}