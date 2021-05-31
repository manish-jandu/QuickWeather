package com.manishjandu.quickweather.ui.weather

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import com.manishjandu.quickweather.utils.getTimeDifference
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
                        weatherViewModel.getWeatherData(lastLocation = event.lastLocation)
                    }
                }
            }
        }

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
                getTimeDifference(weatherData.location.localtime, weatherData.current.last_updated)
            textViewUpdate.text = "last updated $diff min ago."
            textViewTemperature.text = "${(current.temp_c).toInt()}\u00B0"
            textViewWeatherDescription.text = current.condition.text
            textViewWindSpeed.text = "${current.wind_kph}Km/h"
            textViewRainPercentage.text =
                "${weatherData.forecast.forecastday[0].day.daily_chance_of_rain}%"
            textViewRainInMm.text =
                "${current.precip_mm} mm"
            textViewFeelsLike.text = current.feelslike_c.toString()
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

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted: Boolean ->
            if (isGranted) {
                weatherViewModel.getLastLocation()
            } else {
                //Todo:check city in room
            }
        }

    private fun checkAndSetPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Constants.COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                weatherViewModel.getLastLocation()
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
