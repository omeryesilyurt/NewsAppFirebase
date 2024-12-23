package com.example.newsappfirebase.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsappfirebase.model.NewsModel
import com.example.newsappfirebase.repository.FirebaseRepository
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

    fun addOrRemove(news: NewsModel, isAdd: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val userEmail = FirebaseAuth.getInstance().currentUser?.email
            if (news.newsId == null) {
                news.newsId = UUID.randomUUID().toString()
            }
            val newsData = mapOf(
                "newsId" to news.newsId,
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
                    firebaseRepository.addToFavorites(news.newsId, newsData)
                } else {
                    firebaseRepository.removeFromFavorites(news.newsId)
                }
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
    }

    suspend fun fetchFavoritesFromFirebase(): List<NewsModel> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine<List<NewsModel>> { continuation ->
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
        }
    }


}