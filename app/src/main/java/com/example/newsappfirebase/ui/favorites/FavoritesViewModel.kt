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
import com.example.newsappfirebase.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val apiService: ApiService
) : ViewModel() {

    val eventFetchNews = MutableLiveData<List<NewsModel>?>()

    fun getFavoriteNews(): Flow<PagingData<NewsModel>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                NewsPagingSource(
                    firebaseRepository,
                    apiService,
                    category = "favorites",
                    isFavoritesMode = true
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
                if (isAdd) {
                    firebaseRepository.addToFavorites(news.newsId.toString(), newsData)
                } else {
                    firebaseRepository.removeFromFavorites(news.newsId.toString())
                }
                val updatedFavorites = fetchFavoritesFromFirebase()
                eventFetchNews.postValue(updatedFavorites)
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
    }

    private suspend fun fetchFavoritesFromFirebase(): List<NewsModel> {
        return try {
            val favoriteDocuments = firebaseRepository.getFavorites().await()
            favoriteDocuments.mapNotNull { document ->
                document.toObject(NewsModel::class.java)?.apply {
                    this.id = document.getString("id").toString()
                    this.newsId = UUID.fromString(document.getString("newsId"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


}