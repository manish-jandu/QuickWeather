package com.manishjandu.quickweather.ui.weather

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings

import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.manishjandu.quickweather.R
import com.manishjandu.quickweather.data.models.WeatherData
import com.manishjandu.quickweather.databinding.FragmentWeatherBinding
import com.manishjandu.quickweather.utils.Constants
import com.manishjandu.quickweather.utils.UtilsEvent
import com.manishjandu.quickweather.utils.getTimeDifference
import com.manishjandu.quickweather.utils.utilEvent
import kotlinx.coroutines.flow.collect

private const val TAG = "WeatherFragment"

class WeatherFragment : Fragment(R.layout.fragment_weather) {

    private val weatherViewModel: WeatherViewModel by viewModels()
    private lateinit var binding: FragmentWeatherBinding
    private lateinit var adapter: ForecastAdapter

    override fun onStart() {
        super.onStart()
        checkInternetAndLocationAccess()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentWeatherBinding.bind(view)

        adapter = ForecastAdapter()
        binding.recyclerViewWeatherForecast.adapter = adapter
        binding.recyclerViewWeatherForecast.layoutManager = LinearLayoutManager(requireContext())

        binding.buttonLocation.setOnClickListener {
            weatherViewModel.slideToSearchScreen()
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
                        showErrorSnackBar(
                            error = "Couldn't load,please try again"
                        ) {
                            weatherViewModel.getLastLocation()
                        }
                    }
                    WeatherViewModel.WeatherEvent.InternetNotEnabledError -> {
                        showErrorSnackBar(
                            error = "Enable Mobile Data and try again"
                        ) { checkInternetAndLocationAccess() }
                    }
                    WeatherViewModel.WeatherEvent.LocationNotEnabledError -> {
                        showErrorSnackBar(
                            buttonText = "Settings",
                            error = "Enable Location services"
                        ) {
                            moveToLocationSettings()
                            //todo:checkInternetAndLocationAccess()
                        }
                    }
                    is WeatherViewModel.WeatherEvent.InternetAndLocationEnabled -> {
                        if (event.bothEnabled) {
                            checkAndSetPermission()
                        }
                    }
                    is WeatherViewModel.WeatherEvent.LastLocation -> {
                        setLocationLocaleAndGetData(event.lastLocation)
                    }
                    is WeatherViewModel.WeatherEvent.LocaleLocation -> {
                        if (event.location == "hello") {
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
            .setActionTextColor(Color.RED)
            .show()
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
        weatherViewModel.hasInternetAndLocationEnabled()
    }

    private fun showLocationPermissionAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Location Service is required for this app to work properly")
            .setPositiveButton("Allow") { _, _ ->
                requestPermissionLauncher.launch(Constants.COARSE_LOCATION)
            }.setNegativeButton("Cancel", null)
            .show()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted: Boolean ->
            if (isGranted) {
                //Todo:check city in room
                weatherViewModel.getLocationDataFromRoom()
            } else {
                weatherViewModel.slideToSearchScreen()
            }
        }

    private fun checkAndSetPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Constants.COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                weatherViewModel.getLocationDataFromRoom()
            }
            shouldShowRequestPermissionRationale(Constants.COARSE_LOCATION) -> {
                //dialog to show why we need access to location
                showLocationPermissionAlertDialog()
            }

            else -> {
                //request permission
                requestPermissionLauncher.launch(Constants.COARSE_LOCATION)
            }
        }
    }
}
