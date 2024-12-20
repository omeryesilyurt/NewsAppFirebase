package com.example.newsappfirebase.ui.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsappfirebase.model.NewsModel
import com.example.newsappfirebase.network.ApiService
import com.example.newsappfirebase.paging.NewsPagingSource
import com.example.newsappfirebase.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val firebaseRepository: FirebaseRepository
) :
    ViewModel() {
    var selectedCategory: String? = null


    fun getNews(category: String): Flow<PagingData<NewsModel>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                NewsPagingSource(
                    firebaseRepository
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    fun addOrRemove(news: NewsModel, isAdd: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val userEmail = FirebaseAuth.getInstance().currentUser?.email
            if (news.newsId == null) {
                news.newsId = UUID.randomUUID()
            }

            val newsData = mapOf(
                "newsId" to news.newsId.toString(),
                "id" to news.id,
                "name" to news.name,
                "description" to news.description,
                "image" to news.image,
                "source" to news.source,
                "url" to news.url,
                "email" to userEmail
            )

            try {
              /*  if (isAdd) {
                    firebaseRepository.addToFavorites(news.newsId.toString(), newsData)
                } else {
                    firebaseRepository.removeFromFavorites(news.newsId.toString())
                }*/
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}