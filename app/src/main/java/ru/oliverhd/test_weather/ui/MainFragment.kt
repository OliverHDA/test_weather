package ru.oliverhd.test_weather.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.main_fragment.*
import ru.oliverhd.test_weather.R
import ru.oliverhd.test_weather.app.AppData
import ru.oliverhd.test_weather.googlemaps.MapsFragment
import ru.oliverhd.test_weather.view_model.MainViewModel

private const val SPB_LATITUDE = 59.9342802
private const val SPB_LONGITUDE = 30.3350986
private const val MSK_LATITUDE = 55.751244
private const val MSK_LONGITUDE = 37.618423

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var weatherRecyclerAdapter: WeatherRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()
        weatherRecyclerView.adapter = weatherRecyclerAdapter

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.getWeather(SPB_LATITUDE, SPB_LONGITUDE)
        viewModel.getData()
            .observe(viewLifecycleOwner, { renderData(it) })

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    when (tab.position) {
                        0 -> viewModel.getWeather(SPB_LATITUDE, SPB_LONGITUDE)
                        1 -> viewModel.getWeather(MSK_LATITUDE, MSK_LONGITUDE)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        mainFragmentFABLocation.setOnClickListener {
            requireActivity().supportFragmentManager.apply {
                beginTransaction()
                    .add(R.id.container, MapsFragment())
                    .addToBackStack("")
                    .commitAllowingStateLoss()
            }
        }
    }

    private fun renderData(data: AppData) {
        when (data) {
            is AppData.Success -> {
                main.visibility = View.VISIBLE
                main_fragment_loading_layout.visibility = View.GONE
                val serverResponseData = data.serverResponseData
                weatherRecyclerAdapter.setData(serverResponseData.daily)
                val iconUrl =
                    "https://openweathermap.org/img/wn/${serverResponseData.current.weather[0].icon}@2x.png"
                weatherIcon.load(iconUrl) {
                    lifecycle(this@MainFragment)
                    error(R.drawable.ic_load_error_vector)
                    placeholder(R.drawable.ic_no_photo_vector)
                }
                temperature.text = serverResponseData.current.temp.toInt().toString()
                weatherConditionTextView.text = serverResponseData.current.weather[0].description
            }
            is AppData.Loading -> {
                main_fragment_loading_layout.visibility = View.VISIBLE
            }
            is AppData.Error -> {
                main.visibility = View.VISIBLE
                main_fragment_loading_layout.visibility = View.GONE
                Toast.makeText(context, data.error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initRecycler() {
        weatherRecyclerAdapter = WeatherRecyclerAdapter()
        recyclerView = weatherRecyclerView
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = weatherRecyclerAdapter
        }
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}