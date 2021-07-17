package com.manishjandu.quickweather.ui.weather

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.assent.GrantResult
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.isAllGranted
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.manishjandu.quickweather.R
import com.manishjandu.quickweather.data.models.WeatherData
import com.manishjandu.quickweather.databinding.FragmentWeatherBinding
import com.manishjandu.quickweather.utils.UtilsEvent
import com.manishjandu.quickweather.utils.getTimeDifference
import com.manishjandu.quickweather.utils.utilEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

private const val TAG = "WeatherFragment"

@AndroidEntryPoint
class WeatherFragment : Fragment(R.layout.fragment_weather) {

    private val weatherViewModel: WeatherViewModel by viewModels()
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ForecastAdapter

    override fun onStart() {
        super.onStart()
        checkInternetAndLocationAccess()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentWeatherBinding.bind(view)
        val swipeRefreshWeather = binding.swipeRefreshWeather

        adapter = ForecastAdapter()
        binding.recyclerViewWeatherForecast.adapter = adapter
        binding.recyclerViewWeatherForecast.layoutManager = LinearLayoutManager(requireContext())

        binding.buttonLocation.setOnClickListener {
            weatherViewModel.slideToSearchScreen()
        }

        swipeRefreshWeather.setOnRefreshListener {
            checkInternetAndLocationAccess()
            swipeRefreshWeather.isRefreshing = false
        }

        weatherViewModel.weatherData.observe(viewLifecycleOwner) { weatherData ->
            weatherData?.let {
                setupWeatherDataInView(weatherData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            weatherViewModel.weatherEvent.collect { event ->
                when (event) {
                    is WeatherViewModel.WeatherEvent.ShowErrorMessage -> {
                        Snackbar.make(
                            requireView(),
                            "Couldn't load,please try again",
                            Snackbar.LENGTH_LONG
                        ).setAction("try again") {
                            weatherViewModel.getLastLocation()
                        }.setActionTextColor(Color.RED).show()
                    }
                    is WeatherViewModel.WeatherEvent.LastLocation -> {
                        setLocationLocaleAndGetData(event.lastLocation)
                    }
                    is WeatherViewModel.WeatherEvent.LocaleLocation -> {
                        if (event.location == requireContext().getString(R.string.firstTimeLocation)) {
                            weatherViewModel.getLastLocation()
                        } else {
                            weatherViewModel.getWeatherData(event.location)
                        }
                    }

                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            utilEvent.collect { event ->
                when (event) {
                    is UtilsEvent.NewWeatherLocation -> {
                        setLocationLocaleAndGetData(event.newLocation)
                    }
                    is UtilsEvent.CurrentWeatherLocation -> {
                        weatherViewModel.getLastLocation()
                    }
                }
            }
        }
    }

    private fun setLocationLocaleAndGetData(newLocation: String) {
        weatherViewModel.setLocationDataInRoom(newLocation)
        weatherViewModel.getWeatherData(newLocation)
    }


    private fun showErrorSnackBar(
        buttonText: String? = "try again",
        error: String,
        reaction: () -> Unit
    ) {
        Snackbar.make(requireView(), error, Snackbar.LENGTH_INDEFINITE)
            .setAction(buttonText) {
                reaction()
            }
            .setActionTextColor(Color.RED).show()
    }

    @SuppressLint("SetTextI18n")
    private fun setupWeatherDataInView(weatherData: WeatherData) {
        val current = weatherData.current

        binding.apply {
            textViewLocationName.text = weatherData.location.name
            //
            val diff =
                getTimeDifference(weatherData.location.localtime, weatherData.current.lastUpdated)
            textViewUpdate.text = "last updated $diff min ago."
            textViewTemperature.text = "${(current.tempC).toInt()}\u00B0"
            textViewWeatherDescription.text = current.condition.text
            textViewWindSpeed.text = "${current.windKph}Km/h"
            textViewRainPercentage.text =
                "${weatherData.forecast.forecastday[0].day.dailyChanceOfRain}%"
            textViewRainInMm.text =
                "${current.precipMm} mm"
            textViewFeelsLike.text = current.feelslikeC.toString()
            textViewUvIndex.text = current.uv.toString()

            adapter.submitList(weatherData.forecast.forecastday)

            Glide.with(requireView())
                .load("https:${weatherData.current.condition.icon}")
                .centerCrop()
                .placeholder(R.drawable.sun)
                .into(imageViewWeatherIcon)
        }
    }


    private fun moveToLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun checkInternetAndLocationAccess() {
        val internetEnabled = checkInternetEnabled()
        val locationEnabled = checkLocationEnabled()

        if (internetEnabled && locationEnabled) {
            checkAndSetPermission()

        } else if (!internetEnabled) {
            showErrorSnackBar(error = "Enable Mobile Data and try again") {
                checkInternetAndLocationAccess()
            }

        } else if (!locationEnabled) {
            showErrorSnackBar(buttonText = "Settings", error = "Enable Location services") {
                moveToLocationSettings()
            }
        }
    }

    private fun checkInternetEnabled(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    private fun checkLocationEnabled(): Boolean {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return gps_enabled
    }


    private fun showLocationPermissionAlertDialog(
        message: String,
        actionTitle: String,
        action: () -> Unit
    ) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton(actionTitle) { _, _ ->
                action()
            }.setNegativeButton("Cancel", null)
            .show()
    }

    private fun checkAndSetPermission() {
        if (!checkPermission()) {
            setPermissions()
        } else {
            weatherViewModel.getLocationDataFromRoom()
        }
    }

    private fun checkPermission(): Boolean {
        return isAllGranted(Permission.ACCESS_COARSE_LOCATION)
    }

    private fun setPermissions() {

        askForPermissions(
            Permission.ACCESS_COARSE_LOCATION
        ) { result ->

            if (result.isAllGranted()) {
                weatherViewModel.getLocationDataFromRoom()
            }

            if (result[Permission.ACCESS_COARSE_LOCATION] == GrantResult.DENIED
            ) {
                showLocationPermissionAlertDialog(
                    "Location Service is required for this app to work properly",
                    "Allow"
                ) {
                    setPermissions()
                }
            }

            if (result[Permission.ACCESS_COARSE_LOCATION] == GrantResult.PERMANENTLY_DENIED
            ) {
                weatherViewModel.slideToSearchScreen()
            }

        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
