package com.manishjandu.quickweather.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.manishjandu.quickweather.R
import com.manishjandu.quickweather.databinding.FragmentSearchBinding

private const val TAG = "SearchFragment"

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)

        val searchView = binding.searchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                Log.i(TAG, "onQueryTextSubmit: $query")

                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {

                Log.i(TAG, "onQueryTextChange: $query")

                return false
            }

        })




        return super.onCreateView(inflater, container, savedInstanceState)
    }

}


