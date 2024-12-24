package com.example.newsappfirebase.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsappfirebase.model.NewsModel
import com.example.newsappfirebase.repository.FirebaseRepository
import com.example.newsappfirebase.utils.FavoritesMapper
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    private val _favoritesList = MutableLiveData<List<NewsModel>>()
    val favoritesList: LiveData<List<NewsModel>> get() = _favoritesList

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
                fetchFavoritesFromFirebase()
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
    }

    fun fetchFavoritesFromFirebase() {
        viewModelScope.launch(Dispatchers.IO) {
            val favorites = suspendCoroutine<List<NewsModel>> { continuation ->
                firebaseRepository.getFavorites { documents, exception ->
                    if (exception == null && documents != null) {
                        val result = documents.mapNotNull { doc ->
                            try {
                                doc.toObject(NewsModel::class.java)
                            } catch (e: Exception) {
                                null
                            }
                        }
                        continuation.resume(result)
                    } else {
                        continuation.resumeWithException(exception ?: Exception("Unknown Error"))
                    }
                }
            }
            _favoritesList.postValue(favorites.map { it.copy(isFavorite = true) })
        }
    }


}