package com.example.newsappfirebase.ui.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsappfirebase.model.NewsModel
import com.example.newsappfirebase.paging.NewsPagingSource
import com.example.newsappfirebase.repository.FirebaseRepository
import com.example.newsappfirebase.repository.RemoteRepository
import com.example.newsappfirebase.utils.FavoritesMapper
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val remoteRepository: RemoteRepository
) :
    ViewModel() {
    var selectedCategory: String? = null

    fun getNews(category: String): Flow<PagingData<NewsModel>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                NewsPagingSource(
                    remoteRepository,
                    firebaseRepository,
                    category
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    fun addOrRemove(news: NewsModel, isAdd: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val userEmail = FirebaseAuth.getInstance().currentUser?.email
            val newsData = FavoritesMapper.toMap(news,userEmail)
            try {
               if (isAdd) {
                   news.name?.let { firebaseRepository.addToFavorites(it, newsData) }
                } else {
                   news.name?.let { firebaseRepository.removeFromFavorites(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}