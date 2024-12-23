package com.example.newsappfirebase.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.newsappfirebase.R
import com.example.newsappfirebase.databinding.FragmentHomeBinding
import com.example.newsappfirebase.model.NewsModel
import com.example.newsappfirebase.paging.NewsPagingAdapter
import com.example.newsappfirebase.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

@AndroidEntryPoint
class HomeFragment : BaseFragment(), AddOrRemoveFavoriteListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var pagingAdapter: NewsPagingAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.tvTitle.text = getText(R.string.title_home)
        pagingAdapter = NewsPagingAdapter(
            onItemClick = { newsItem ->
                if (newsItem.newsId == null) {
                    newsItem.newsId = UUID.randomUUID().toString()
                }
                val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment()
                val bundle = Bundle().apply {
                    putSerializable("selectedNews", newsItem)
                }
                findNavController().navigate(action.actionId, bundle)
            },
            addOrRemoveFavoriteListener = { newsItem, isFavorite ->
                homeViewModel.addOrRemove(newsItem, isFavorite)
            }
        )
        binding.rvNews.adapter = pagingAdapter
        showLoadingOnce(binding.lottieLoading)
        updateNews("general")
        clickEvents()
    }

    private fun clickEvents() {
        binding.apply {
            btnGeneral.setOnClickListener {
                highlightButton(
                    binding.btnGeneral,
                    binding.btnSport,
                    binding.btnEconomy,
                    binding.btnTechnology
                )
                showLoadingOnce(binding.lottieLoading)
                homeViewModel.selectedCategory = "general"
                updateNews("general")
            }
            btnSport.setOnClickListener {
                highlightButton(
                    binding.btnSport,
                    binding.btnGeneral,
                    binding.btnEconomy,
                    binding.btnTechnology
                )
                showLoadingOnce(binding.lottieLoading)
                homeViewModel.selectedCategory = "sport"
                updateNews("sport")
            }
            btnEconomy.setOnClickListener {
                highlightButton(
                    binding.btnEconomy,
                    binding.btnGeneral,
                    binding.btnSport,
                    binding.btnTechnology
                )
                showLoadingOnce(binding.lottieLoading)
                homeViewModel.selectedCategory = "economy"
                updateNews("economy")
            }
            btnTechnology.setOnClickListener {
                highlightButton(
                    binding.btnTechnology,
                    binding.btnGeneral,
                    binding.btnSport,
                    binding.btnEconomy
                )
                showLoadingOnce(binding.lottieLoading)
                homeViewModel.selectedCategory = "technology"
                updateNews("technology")
            }
        }
    }

    private fun highlightButton(selectedButton: View, vararg otherButtons: View) {
        selectedButton.setBackgroundResource(R.drawable.selected_button_bg)
        otherButtons.forEach { it.setBackgroundResource(R.drawable.default_button_bg) }
    }

    private fun updateNews(category: String) {
        lifecycleScope.launch {
            homeViewModel.getNews(category).collectLatest { pagingData ->
                pagingAdapter.submitData(pagingData)
            }
        }
    }


    override fun onAddOrRemoveFavorite(news: NewsModel, isAdd: Boolean) {
        homeViewModel.addOrRemove(news, isAdd)
    }

    override fun onResume() {
        super.onResume()
        showLoadingOnce(binding.lottieLoading)
        updateNews("general")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
