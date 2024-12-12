package com.example.newsappfirebase.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import com.example.newsappfirebase.R
import com.example.newsappfirebase.databinding.FragmentSearchBinding
import com.example.newsappfirebase.paging.NewsPagingAdapter
import com.example.newsappfirebase.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var adapter: NewsPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = NewsPagingAdapter()
        binding.apply {
            toolbar.tvTitle.text = getText(R.string.title_search)
            rvSearchNews.adapter = adapter
            layoutSearch.setOnClickListener {
                searchView.isIconified = false
            }
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { nonNullQuery ->
                        searchViewModel.searchPagingData(nonNullQuery, "general")
                            .observe(viewLifecycleOwner) { pagingData ->
                                adapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
                            }
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let { nonNullQuery ->
                        searchViewModel.searchPagingData(nonNullQuery, "general")
                            .observe(viewLifecycleOwner) { pagingData ->
                                adapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
                            }
                    }
                    return true
                }

            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
