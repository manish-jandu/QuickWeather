package com.manishjandu.quickweather

import androidx.fragment.app.Fragment
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.manishjandu.quickweather.databinding.ActivityMainBinding
import com.manishjandu.quickweather.ui.base.BaseFragment
import com.manishjandu.quickweather.ui.search.SearchFragment
import com.manishjandu.quickweather.ui.weather.WeatherFragment
import com.manishjandu.quickweather.utils.Constants.NUM_PAGES
import com.manishjandu.quickweather.utils.Constants.positionSearchFragment
import com.manishjandu.quickweather.utils.Constants.positionWeatherFragment
import com.manishjandu.quickweather.utils.UtilsEvent
import com.manishjandu.quickweather.utils.utilEvent
 import kotlinx.coroutines.flow.collect

class MainActivity : FragmentActivity() {
    lateinit var viewPager2: ViewPager2
    private lateinit var binding: ActivityMainBinding

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager2 = binding.viewPager
        viewPager2.adapter = ScreenSlidePagerAdapter(this)

        lifecycleScope.launchWhenStarted {
            utilEvent.collect { event ->
                when (event) {
                    UtilsEvent.SlideToSearchScreen -> {
                        viewPager2.currentItem = positionSearchFragment
                    }
                    UtilsEvent.SlideToWeatherScreen ->{
                        viewPager2.currentItem = positionWeatherFragment
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
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                positionWeatherFragment -> BaseFragment()
                positionSearchFragment -> SearchFragment()
                else -> WeatherFragment()
            }
        }
    }

}


