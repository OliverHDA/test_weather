package ru.oliverhd.test_weather.googlemaps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.api.load
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_maps.*
import ru.oliverhd.test_weather.R
import ru.oliverhd.test_weather.app.AppData
import ru.oliverhd.test_weather.model.CurrentWeather
import ru.oliverhd.test_weather.view_model.MainViewModel

private const val REQUEST_CODE = 12345
private const val SPB_LATITUDE = 59.9342802
private const val SPB_LONGITUDE = 30.3350986

class MapsFragment : Fragment() {

    private lateinit var map: GoogleMap
    private var marker: Marker? = null
    private lateinit var viewModel: MainViewModel
    lateinit var mWindow: View
    private var myCord: LatLng? = LatLng(SPB_LATITUDE, SPB_LONGITUDE)

    private val onLocationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {
            context?.let {
                myCord = LatLng(location.latitude, location.longitude)
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {
            myCord = null
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCord!!, 10f))
        marker = map.addMarker(
            MarkerOptions().position(myCord!!)
        )
        map.setOnMapClickListener { latLng ->
            marker?.position = latLng
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            viewModel.getWeather(latLng.latitude, latLng.longitude)
            viewModel.getData()
                .observe(viewLifecycleOwner, { renderData(it) })
        }
        myLocationIcon.setOnClickListener {
            checkPermission()
            if (myCord != null) {
                marker?.position = myCord!!
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(myCord!!, 10f))
                viewModel.getWeather(myCord!!.latitude, myCord!!.longitude)
                viewModel.getData()
                    .observe(viewLifecycleOwner, { renderData(it) })
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mWindow = requireActivity().layoutInflater.inflate(R.layout.map_info_window, null)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        checkPermission()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun renderData(data: AppData) {
        when (data) {
            is AppData.Success -> {
                val serverResponseData = data.serverResponseData
                setMyInfoWindow(serverResponseData.current)
                marker?.showInfoWindow()
            }
            is AppData.Loading -> {
            }
            is AppData.Error -> {
                Toast.makeText(context, data.error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setMyInfoWindow(currentWeather: CurrentWeather) {
        map.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(p0: Marker): View? {
                return null
            }

            override fun getInfoContents(p0: Marker): View {
                renderInfoWindow(currentWeather, mWindow)
                return mWindow
            }
        })
    }

    private fun renderInfoWindow(currentWeather: CurrentWeather, view: View) {

        val temperature = view.findViewById<TextView>(R.id.mapInfoTemperature)
        val condition = view.findViewById<TextView>(R.id.mapInfoWeatherConditionTextView)
        val icon = view.findViewById<ImageView>(R.id.mapInfoWeatherIcon)

        temperature.text = currentWeather.temp.toInt().toString()
        condition.text = currentWeather.weather[0].description
        val iconUrl = "https://openweathermap.org/img/wn/${currentWeather.weather[0].icon}@2x.png"
        icon.load(iconUrl) {
            error(R.drawable.ic_load_error_vector)
            placeholder(R.drawable.ic_no_photo_vector)
        }
    }

    private fun checkPermission() {
        activity?.let {
            when {
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    getLocation()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    showRationaleDialog()
                }
                else -> {
                    requestPermission()
                }
            }
        }
    }

    private fun showRationaleDialog() {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.dialog_rationale_title))
                .setMessage(getString(R.string.dialog_rationale_message))
                .setPositiveButton(getString(R.string.dialog_rationale_give_access)) { _, _ ->
                    requestPermission()
                }
                .setNegativeButton(getString(R.string.dialog_rationale_decline)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        checkPermissionsResult(requestCode, grantResults)
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }

    private fun checkPermissionsResult(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> {
                var grantedPermissions = 0
                if ((grantResults.isNotEmpty())) {
                    for (i in grantResults) {
                        if (i == PackageManager.PERMISSION_GRANTED) {
                            grantedPermissions++
                        }
                    }
                    if (grantResults.size == grantedPermissions) {
                        getLocation()
                    } else {
                        showDialog(
                            getString(R.string.dialog_title_no_gps),
                            getString(R.string.dialog_message_no_gps)
                        )
                    }
                } else {
                    showDialog(
                        getString(R.string.dialog_title_no_gps),
                        getString(R.string.dialog_message_no_gps)
                    )
                }
                return
            }
        }
    }

    private fun showDialog(title: String, message: String) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(getString(R.string.dialog_button_close)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun getLocation() {
        activity?.let { context ->
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                val locationManager =
                    context.getSystemService(Context.LOCATION_SERVICE) as
                            LocationManager
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    val provider =
                        locationManager.getProvider(LocationManager.GPS_PROVIDER)
                    provider?.let {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0,
                            0f,
                            onLocationListener
                        )
                    }
                } else {
                    showDialog(
                        getString(R.string.dialog_title_gps_turned_off),
                        getString(R.string.dialog_message_last_location_unknown)
                    )
                }
            } else {
                showRationaleDialog()
            }
        }
    }
}