package com.manishjandu.quickweather.ui.search

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.manishjandu.quickweather.R
import com.manishjandu.quickweather.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

private const val TAG = "SearchFragment"

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var suggestionAdapter: SuggestionsAdapter
    private lateinit var searchView: SearchView
    private val searchViewModel: SearchViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentSearchBinding.bind(view)
        suggestionAdapter = SuggestionsAdapter(SuggestionClick())

        searchView = binding.searchView
        val recyclerViewCitiesSuggestion = binding.recyclerViewCitiesSuggestion

        recyclerViewCitiesSuggestion.adapter = suggestionAdapter
        recyclerViewCitiesSuggestion.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewCitiesSuggestion.visibility = View.GONE

        binding.floatingButtonAddLocation.setOnClickListener {
            getCurrentLocation()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchViewModel.getLocations(query)
                }
                return false
            }

            override fun onQueryTextChange(newQuery: String?): Boolean {
                if (newQuery.isNullOrEmpty()) {
                    recyclerViewCitiesSuggestion.visibility = View.GONE
                    suggestionAdapter.submitList(null)
                }
                return false
            }
        })


        searchViewModel.locations.observe(viewLifecycleOwner) {
            it?.let {
                recyclerViewCitiesSuggestion.visibility = View.VISIBLE
                suggestionAdapter.submitList(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            searchViewModel.searchEvent.collect { event ->
                when (event) {
                    is SearchViewModel.SearchEvent.LocationNotFound -> {
                        Snackbar.make(requireView(), "Location not Found", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    inner class SuggestionClick : SuggestionsAdapter.OnClick {
        override fun onClicked(newLocation: String) {
            suggestionAdapter.submitList(null)
            searchView.clearFocus()
            searchView.setQuery("", false)
            searchViewModel.setNewLocationAndSlide(newLocation)
        }
    }

    private fun getCurrentLocation() {
        searchViewModel.getCurrentLocation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


