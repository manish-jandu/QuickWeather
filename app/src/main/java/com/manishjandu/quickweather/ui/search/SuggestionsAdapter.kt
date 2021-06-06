package com.manishjandu.quickweather.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.manishjandu.quickweather.data.models.LocationsItem
import com.manishjandu.quickweather.databinding.ItemSuggestionBinding


class SuggestionsAdapter(val onClick: OnClick) :
    ListAdapter<LocationsItem, SuggestionsAdapter.SuggestionViewHolder>(DiffUtilCallback()) {

    interface OnClick{
        fun onClicked(newLocation:String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val binding =
            ItemSuggestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SuggestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class SuggestionViewHolder(binding: ItemSuggestionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val suggestedText = binding.textViewSuggestion

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION){
                        val item = getItem(position)
                        val newLocation = "${item.lat},${item.lon}"
                        onClick.onClicked(newLocation)
                    }
                }
            }
        }

        fun bind(item: LocationsItem) {
            suggestedText.text = item.name
        }

    }

    class DiffUtilCallback() : DiffUtil.ItemCallback<LocationsItem>() {

        override fun areItemsTheSame(
            oldItem: LocationsItem,
            newItem: LocationsItem
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: LocationsItem,
            newItem: LocationsItem
        ): Boolean {
            return oldItem.toString() == newItem.toString()
        }
    }
}