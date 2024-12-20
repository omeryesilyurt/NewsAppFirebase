package com.example.newsappfirebase.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import com.example.newsappfirebase.R
import com.example.newsappfirebase.databinding.FragmentFavoritesBinding
import com.example.newsappfirebase.ui.home.AddOrRemoveFavoriteListener
import com.example.newsappfirebase.model.NewsModel
import com.example.newsappfirebase.paging.NewsPagingAdapter
import com.example.newsappfirebase.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : BaseFragment(), AddOrRemoveFavoriteListener {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val favoriteViewModel: FavoritesViewModel by viewModels()
    private lateinit var favoriteListAdapter: NewsPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoriteListAdapter =
            NewsPagingAdapter(addOrRemoveFavoriteListener = { newsItem, isFavorite ->
                favoriteViewModel.addOrRemove(newsItem, isFavorite)
            })
        binding.toolbar.tvTitle.text = getText(R.string.title_fav)
        binding.rvFavNews.adapter = favoriteListAdapter
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAddOrRemoveFavorite(news: NewsModel, isAdd: Boolean) {
        favoriteViewModel.addOrRemove(news, isAdd)
    }

   private fun setupObservers() {
        lifecycleScope.launch {
            favoriteViewModel.getFavoriteNews().collectLatest { pagingData ->
                favoriteListAdapter.submitData(pagingData)
            }
        }
    }
}
