package com.example.newsappfirebase.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.newsappfirebase.R
import com.example.newsappfirebase.databinding.FragmentFavoritesBinding
import com.example.newsappfirebase.ui.home.AddOrRemoveFavoriteListener
import com.example.newsappfirebase.model.NewsModel
import com.example.newsappfirebase.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.FieldPosition

@AndroidEntryPoint
class FavoritesFragment : BaseFragment(), AddOrRemoveFavoriteListener {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val favoriteViewModel: FavoritesViewModel by viewModels()
    private lateinit var favoritesAdapter: FavoritesAdapter

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
        favoritesAdapter = FavoritesAdapter { news, isFavorite ->
            favoriteViewModel.addOrRemove(news, isFavorite)
        }
        binding.rvFavNews.adapter = favoritesAdapter
        binding.toolbar.tvTitle.text = getText(R.string.title_fav)
        setupObservers()
        favoriteViewModel.fetchFavoritesFromFirebase()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAddOrRemoveFavorite(news: NewsModel, isAdd: Boolean) {
        favoriteViewModel.addOrRemove(news, isAdd)
    }

    private fun setupObservers() {
        favoriteViewModel.favoritesList.observe(viewLifecycleOwner) { favorites ->
            favoritesAdapter.submitList(favorites)
        }
    }
}

