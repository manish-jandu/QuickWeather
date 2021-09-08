package com.manishjandu.quickweather

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.manishjandu.quickweather.databinding.ActivityMainBinding
import com.manishjandu.quickweather.ui.search.SearchFragment
import com.manishjandu.quickweather.ui.weather.WeatherFragment
import com.manishjandu.quickweather.utils.Constants.POSITION_SEARCH_FRAGMENT
import com.manishjandu.quickweather.utils.Constants.POSITION_WEATHER_FRAGMENT
import com.manishjandu.quickweather.utils.InternetConnectivity.ConnectivityManager
import com.manishjandu.quickweather.utils.UtilsEvent
import com.manishjandu.quickweather.utils.utilEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
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

        lifecycleScope.launchWhenStarted {
            utilEvent.collect { event ->
                when (event) {
                    UtilsEvent.SlideToSearchScreen -> {
                        viewPager2.currentItem = POSITION_SEARCH_FRAGMENT
                    }
                    UtilsEvent.SlideToWeatherScreen -> {
                        viewPager2.currentItem = POSITION_WEATHER_FRAGMENT
                    }
                }
            }
        }

    }

    override fun onBackPressed() {
        if (viewPager2.currentItem == 0) {
            super.onBackPressed()
        } else {
            viewPager2.currentItem = viewPager2.currentItem - 1
        }
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> WeatherFragment()
                1 -> SearchFragment()
                else -> WeatherFragment()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        connectivityManager.unregisterConnectionObserver()
    }
}


