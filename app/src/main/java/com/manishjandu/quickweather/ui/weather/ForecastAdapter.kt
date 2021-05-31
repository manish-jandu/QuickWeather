package com.manishjandu.quickweather.ui.weather

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.manishjandu.quickweather.R
import com.manishjandu.quickweather.data.models.Forecast
import com.manishjandu.quickweather.data.models.ForecastDay
import com.manishjandu.quickweather.databinding.ItemWeatherForecastBinding

private const val TAG = "ForecastAdapter"

class ForecastAdapter :
    ListAdapter<ForecastDay, ForecastAdapter.ForecastViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val binding =
            ItemWeatherForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder: in the bind view holder")
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ForecastViewHolder(binding: ItemWeatherForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val itemBinding = binding

        fun bind(item: ForecastDay) {
            itemBinding.apply {
                textViewForecastDate.text = item.date
                textViewForecastAvgTemp.text = "${(item.day.avgtemp_c).toInt()}\u00B0"
                textViewForecastDescription.text = item.day.condition.text
                textViewForecastRainInMm.text = "${item.day.totalprecip_mm.toString()} mm"
                textViewForecastRainPercentage.text = "${item.day.daily_chance_of_rain}%"

                Glide.with(itemBinding.root)
                    .load("https:${item.day.condition.icon}")
                    .centerCrop()
                    .placeholder(R.drawable.sun)
                    .into(imageViewForecastIcon)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ForecastDay>() {
        override fun areItemsTheSame(oldItem: ForecastDay, newItem: ForecastDay): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ForecastDay, newItem: ForecastDay): Boolean {
            return oldItem.date == newItem.date
        }
    }
}