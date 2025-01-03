package com.example.newsappfirebase.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.newsappfirebase.model.NewsModel
import com.example.newsappfirebase.paging.NewsPagingSource
import com.example.newsappfirebase.repository.FirebaseRepository
import com.example.newsappfirebase.repository.RemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    fun searchPagingData(query: String?,category: String): LiveData<PagingData<NewsModel>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { NewsPagingSource(remoteRepository,  firebaseRepository,category, query) }
        ).liveData.cachedIn(viewModelScope)
    }

}