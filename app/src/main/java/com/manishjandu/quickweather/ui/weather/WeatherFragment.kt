package com.manishjandu.quickweather.ui.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.isAllGranted
import com.bumptech.glide.Glide
import com.manishjandu.quickweather.R
import com.manishjandu.quickweather.data.models.WeatherData
import com.manishjandu.quickweather.databinding.FragmentWeatherBinding
import com.manishjandu.quickweather.utils.Constants.CANNOT_GET_LAST_LOCATION
import com.manishjandu.quickweather.utils.Constants.POSITION_SEARCH_FRAGMENT
import com.manishjandu.quickweather.utils.InternetConnectivity.ConnectivityManager
import com.manishjandu.quickweather.utils.LocationResult
import com.manishjandu.quickweather.utils.NetworkResult
import com.manishjandu.quickweather.utils.getTimeDifference
import com.manishjandu.quickweather.viewmodels.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "WeatherFragment"

@AndroidEntryPoint
class WeatherFragment : Fragment(R.layout.fragment_weather) {

    private val viewModel: WeatherViewModel by activityViewModels()
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private val forecastAdapter: ForecastAdapter = ForecastAdapter()
    private lateinit var swipeRefreshWeather: SwipeRefreshLayout

    @Inject
    lateinit var connectivityManager: ConnectivityManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentWeatherBinding.bind(view)
        swipeRefreshWeather = binding.swipeRefreshWeather

        setViewLoading()
        observeInternetConnection()
        setupRecyclerView()

        binding.buttonLocation.setOnClickListener {
            slideToSearchScreen()
        }
        swipeRefreshWeather.setOnRefreshListener {
            checkDataInDatabase()
        }

    }

    override fun onStart() {
        super.onStart()
        checkDataInDatabase()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewWeatherForecast.apply {
            adapter = forecastAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeInternetConnection() {
        connectivityManager.isNetworkAvailable.observe(viewLifecycleOwner) {
            it?.let { isInternetAvailable ->
                if (isInternetAvailable) {
                    checkDataInDatabase()
                } else {
                    setViewError()
                }
            }
        }
    }

    private fun checkDataInDatabase() {
        viewModel.getLocationDataFromDatabase()
        viewModel.location.observe(viewLifecycleOwner) {
            it?.let { locationInLatLong ->
                stopRefreshingIcon()
                when (locationInLatLong) {
                    is LocationResult.Error -> {
                        if (locationInLatLong.message == CANNOT_GET_LAST_LOCATION) {
                            setViewNoLocationPermission("Couldn't get last location.")
                        } else {
                            setViewError(locationInLatLong.message)
                        }
                    }
                    is LocationResult.Success -> {
                        if (locationInLatLong.location == requireContext().getString(R.string.firstTimeLocation)) {
                            //Default Location,so get new
                            checkAndSetPermission()
                        } else {
                            getWeatherData(locationInLatLong.location)
                            saveInRoom(locationInLatLong.location)
                        }
                    }
                }
            }
        }
    }

    private fun saveInRoom(location: String) {
        viewModel.setLocationDataInRoom(location)
    }

    private fun checkAndSetPermission() {
        if (hasPermission()) {
            checkIfLocationIsEnabled()
        } else {
            setViewNoLocationPermission()
            setPermissions()
        }
    }

    private fun checkIfLocationIsEnabled() {
        if (isLocationEnabled()) {
            getLastLocation()
        } else {
            setViewLocationNotEnabled()
        }
    }

    private fun getLastLocation() {
        viewModel.getLastLocation()
    }

    private fun getWeatherData(location: String) {
        viewModel.getWeatherData(location)
        viewModel.weatherData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Error -> {
                    setViewError(response.message.toString())
                }
                is NetworkResult.Loading -> {
                    setViewLoading()
                }
                is NetworkResult.Success -> {
                    setViewWeather()
                    setupWeatherDataInView(response.data!!)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupWeatherDataInView(weatherData: WeatherData) {
        val current = weatherData.current
        val diff =
            getTimeDifference(weatherData.location.localtime, weatherData.current.lastUpdated)

        forecastAdapter.submitList(weatherData.forecast.forecastday)

        binding.apply {
            textViewLocationName.text = weatherData.location.name
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
            Glide.with(requireView())
                .load("https:${weatherData.current.condition.icon}")
                .centerCrop()
                .placeholder(R.drawable.sun)
                .into(imageViewWeatherIcon)
        }
    }


    private fun setViewLoading() {
        stopRefreshingIcon()
        binding.apply {
            progressBar.visibility = View.VISIBLE
            groupError.visibility = View.INVISIBLE
            groupWeather.visibility = View.INVISIBLE
            textViewErrorSetting.visibility = View.INVISIBLE
        }
    }

    private fun setViewError(message: String = "No Internet Connection") {
        stopRefreshingIcon()
        binding.apply {
            textViewError.text = message
            groupError.visibility = View.VISIBLE
            textViewErrorSetting.visibility = View.INVISIBLE
            groupWeather.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
        }
    }

    private fun setViewNoLocationPermission(message: String = "Location Permission Required") {
        stopRefreshingIcon()
        binding.textViewErrorSetting.apply {
            text = "Slide to Search Page -->"
            visibility = View.VISIBLE
            setOnClickListener {
                slideToSearchScreen()
            }
        }
        binding.apply {
            textViewError.text = message
            groupError.visibility = View.VISIBLE
            groupWeather.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
        }
    }

    private fun setViewLocationNotEnabled() {
        stopRefreshingIcon()
        binding.textViewErrorSetting.apply {
            text = "Click here to go to settings page."
            visibility = View.VISIBLE
            setOnClickListener {
                moveToLocationSettings()
            }
        }
        binding.apply {
            textViewError.text = "Location is not enabled"
            groupError.visibility = View.VISIBLE
            groupWeather.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
        }
    }

    private fun setViewWeather() {
        stopRefreshingIcon()
        binding.apply {
            groupWeather.visibility = View.VISIBLE
            groupError.visibility = View.INVISIBLE
            textViewErrorSetting.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
        }
    }

    private fun stopRefreshingIcon() {
        swipeRefreshWeather.isRefreshing = false
    }

    private fun slideToSearchScreen() {
        val viewPager2 = requireActivity().findViewById<ViewPager2>(R.id.view_pager)
        viewPager2.currentItem = POSITION_SEARCH_FRAGMENT
    }

    private fun moveToLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return gpsEnabled
    }


    private fun hasPermission(): Boolean {
        return isAllGranted(Permission.ACCESS_COARSE_LOCATION)
    }

    private fun setPermissions() {
        askForPermissions(
            Permission.ACCESS_COARSE_LOCATION
        ) { result ->
            setViewLoading()
            if (result.isAllGranted(Permission.ACCESS_COARSE_LOCATION)) {
                checkIfLocationIsEnabled()
            } else {
                setViewNoLocationPermission()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
