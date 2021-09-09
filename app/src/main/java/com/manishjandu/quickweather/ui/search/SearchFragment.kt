package com.manishjandu.quickweather.ui.search

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.manishjandu.quickweather.R
import com.manishjandu.quickweather.databinding.FragmentSearchBinding
import com.manishjandu.quickweather.utils.Constants.POSITION_WEATHER_FRAGMENT
import com.manishjandu.quickweather.utils.NetworkResult
import com.manishjandu.quickweather.viewmodels.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SearchFragment"

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var suggestionAdapter: SuggestionsAdapter
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private val viewModel: WeatherViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentSearchBinding.bind(view)
        suggestionAdapter = SuggestionsAdapter(SuggestionClick())

        searchView = binding.searchView
        recyclerView = binding.recyclerViewCitiesSuggestion

        setupRecyclerView()
        observeSearchQueryChange()
        observeLocationResults()

        binding.floatingButtonAddLocation.setOnClickListener {
            viewModel.getLastLocation()
            slideToWeatherScreen()
        }
    }

    private fun observeLocationResults() {
        viewModel.multipleLocations.observe(viewLifecycleOwner) {
            it?.let { response ->
                when (response) {
                    is NetworkResult.Error -> {
                        hideLoading()
                        hideLocationsList()
                        showSnackBar(response.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        showLoading()
                    }
                    is NetworkResult.Success -> {
                        hideLoading()
                        showLocationsList()
                        suggestionAdapter.submitList(response.data)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewCitiesSuggestion.apply {
            adapter = suggestionAdapter
            layoutManager = LinearLayoutManager(requireContext())
            visibility = View.GONE
        }
    }

    private fun observeSearchQueryChange() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    viewModel.getLocations(query)
                }
                return false
            }

            override fun onQueryTextChange(newQuery: String?): Boolean {
                if (newQuery.isNullOrEmpty()) {
                    hideLocationsList()
                }
                return false
            }
        })
    }

    inner class SuggestionClick : SuggestionsAdapter.OnClick {
        override fun onClicked(newLocation: String) {
            searchView.clearFocus()
            searchView.setQuery("", false)
            hideLocationsList()
            setNewWeatherLocation(newLocation)
            slideToWeatherScreen()
        }
    }

    private fun setNewWeatherLocation(newLocation: String) {
        viewModel.setNewLocation(newLocation)
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showLocationsList() {
        binding.recyclerViewCitiesSuggestion.visibility = View.VISIBLE
    }

    private fun hideLocationsList() {
        suggestionAdapter.submitList(null)
        binding.recyclerViewCitiesSuggestion.visibility = View.GONE
    }

    private fun slideToWeatherScreen() {
        val viewPager2 = requireActivity().findViewById<ViewPager2>(R.id.view_pager)
        viewPager2.currentItem = POSITION_WEATHER_FRAGMENT
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


