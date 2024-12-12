package com.example.newsappfirebase.ui.favorites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsappfirebase.model.NewsModel
import com.example.newsappfirebase.network.ApiService
import com.example.newsappfirebase.paging.NewsPagingSource
import com.example.newsappfirebase.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val apiService: ApiService
) : ViewModel() {

    val eventFetchNews = MutableLiveData<List<NewsModel>?>()

    fun getFavoriteNews(): Flow<PagingData<NewsModel>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                NewsPagingSource(
                    apiService,
                    category = "favorites",
                    localRepository,
                    isFavoritesMode = true
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    fun addOrRemove(news: NewsModel, isAdd: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (news.newsId == null) {
                news.newsId = UUID.randomUUID()
            }
            if (isAdd) {
                news.isFavorite = true
                news.addedAt = System.currentTimeMillis()
                localRepository.add(news)
            } else {
                news.isFavorite = false
                localRepository.remove(news)
            }
            eventFetchNews.postValue(localRepository.getFavoriteList())

        }
    }
}