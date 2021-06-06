package com.manishjandu.quickweather.ui.weather

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.manishjandu.quickweather.R
import com.manishjandu.quickweather.data.models.Forecastday
import com.manishjandu.quickweather.databinding.ItemWeatherForecastBinding

private const val TAG = "ForecastAdapter"

class ForecastAdapter :
    ListAdapter<Forecastday, ForecastAdapter.ForecastViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val binding =
            ItemWeatherForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ForecastViewHolder(binding: ItemWeatherForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val itemBinding = binding

        @SuppressLint("SetTextI18n")
        fun bind(item: Forecastday) {
            itemBinding.apply {
                textViewForecastDate.text = item.date
                textViewForecastAvgTemp.text = "${(item.day.avgtempC).toInt()}\u00B0"
                textViewForecastDescription.text = item.day.condition.text
                textViewForecastRainInMm.text = "${item.day.totalprecipMm.toString()} mm"
                textViewForecastRainPercentage.text = "${item.day.dailyChanceOfRain}%"

                Glide.with(itemBinding.root)
                    .load("https:${item.day.condition.icon}")
                    .centerCrop()
                    .placeholder(R.drawable.sun)
                    .into(imageViewForecastIcon)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Forecastday>() {
        override fun areItemsTheSame(oldItem: Forecastday, newItem: Forecastday): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Forecastday, newItem: Forecastday): Boolean {
            return oldItem.date == newItem.date
        }
    }
}