package com.manishjandu.quickweather

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.manishjandu.quickweather.databinding.ActivityMainBinding
import com.manishjandu.quickweather.ui.search.SearchFragment
import com.manishjandu.quickweather.ui.weather.WeatherFragment
import com.manishjandu.quickweather.utils.Constants.POSITION_SEARCH_FRAGMENT
import com.manishjandu.quickweather.utils.Constants.POSITION_WEATHER_FRAGMENT
import com.manishjandu.quickweather.utils.InternetConnectivity.ConnectivityManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    lateinit var viewPager2: ViewPager2
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var connectivityManager: ConnectivityManager

    override fun onStart() {
        super.onStart()
        connectivityManager.registerConnectionObserver()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager2 = binding.viewPager
        viewPager2.adapter = ScreenSlidePagerAdapter(this)
    }

    override fun onBackPressed() {
        if (viewPager2.currentItem == 0) {
            super.onBackPressed()
        } else {
            viewPager2.currentItem = POSITION_WEATHER_FRAGMENT
        }
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                POSITION_WEATHER_FRAGMENT -> WeatherFragment()
                POSITION_SEARCH_FRAGMENT -> SearchFragment()
                else -> WeatherFragment()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterConnectionObserver()
        _binding = null
    }
}


