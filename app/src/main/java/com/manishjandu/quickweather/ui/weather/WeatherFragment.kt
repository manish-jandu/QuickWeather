package com.manishjandu.quickweather.ui.weather

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.manishjandu.quickweather.R
import com.manishjandu.quickweather.databinding.FragmentWeatherBinding
import com.manishjandu.quickweather.utils.Constants

private const val TAG = "WeatherFragment"

class WeatherFragment : Fragment(R.layout.fragment_weather) {

    private val weatherViewModel: WeatherViewModel by viewModels()
    private lateinit var binding: FragmentWeatherBinding
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        checkAndSetPermission()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentWeatherBinding.bind(view)

        weatherViewModel.mLastLocation.observe(viewLifecycleOwner) {lastLocation ->
            lastLocation?.let {
                weatherViewModel.getWeatheData(lastLocation)
            }
        }


    }

    private fun setupWeatherDataInView(){

    }



    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                weatherViewModel.getLastLocation(mFusedLocationProviderClient)
            } else {
                //Todo:check city in room
                //condition if available show weather
                // else: move to search screen
                Log.i(TAG, "permission not granted in val")
            }
        }

    fun checkAndSetPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Constants.COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                weatherViewModel.getLastLocation(mFusedLocationProviderClient)
            }
            shouldShowRequestPermissionRationale(Constants.COARSE_LOCATION) -> {
                //Todo 2: Dialog to show add permission
                Log.i(TAG, "permission not granted and rationale")
                //requestPermissionLauncher.launch(Constants.COARSE_LOCATION)
            }
            else -> {
                //request permission
                Log.i(TAG, "permission not granted in else")
                requestPermissionLauncher.launch(Constants.COARSE_LOCATION)
            }
        }

    }

}