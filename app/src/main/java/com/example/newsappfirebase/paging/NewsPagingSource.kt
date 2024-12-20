package com.example.newsappfirebase.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsappfirebase.model.NewsModel
import com.example.newsappfirebase.network.ApiService
import com.example.newsappfirebase.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.tasks.await
import java.util.UUID

class NewsPagingSource(
    private val firebaseRepository: FirebaseRepository
) :
    PagingSource<Int, NewsModel>() {
    override fun getRefreshKey(state: PagingState<Int, NewsModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val closestPage = state.closestPageToPosition(anchorPosition)
        return closestPage?.prevKey?.plus(1) ?: closestPage?.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsModel> {
        return try {
            val position = params.key ?: 1
            firebaseRepository.getFavorites(onResult = {
                list,error ->
                if (!list.isNullOrEmpty()) {
                    LoadResult.Page(
                        data = list,
                        prevKey = if (position == 1) null else (position - 1),
                        nextKey = if (position == list.size) null else (position + 1)
                    )
                } else {
                    LoadResult.Error(throw Exception("No Response"))
                }
            })
            return LoadResult.Invalid()

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    /*    private suspend fun fetchFavoritesFromFirebase(): List<NewsModel> {
            val userEmail = FirebaseAuth.getInstance().currentUser?.email
            return try {
                val favoriteDocuments = firebaseRepository.getFavorites().await()
                favoriteDocuments.filter { document ->
                    document.getString("email") == userEmail
                }.mapNotNull { document ->
                    val newsId = document.getString("newsId")?.let { UUID.fromString(it) }
                    val id = document.getString("id") ?: return@mapNotNull null
                    val key = document.getString("key")
                    val url = document.getString("url")
                    val description = document.getString("description")
                    val image = document.getString("image")
                    val name = document.getString("name")
                    val source = document.getString("source")

                    if (newsId != null) {
                        NewsModel(
                            newsId = newsId,
                            id = id,
                            key = key,
                            url = url,
                            description = description,
                            image = image,
                            name = name,
                            source = source,
                            isFavorite = true
                        )
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                emptyList()
            }
        }*/

    private fun fetchFavoritesFromFirebase(result: (List<DocumentSnapshot>?, Exception?) -> Unit) {
        firebaseRepository.getFavorites(onResult = { list, error ->
            if (!list.isNullOrEmpty()) {
                result.invoke(list, null)
            }
        }
        )
    }

    companion object {
        const val PAGINATION_LIMIT = 4
    }
}

