package com.example.newsappfirebase.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsappfirebase.model.NewsModel
import com.example.newsappfirebase.repository.FirebaseRepository
import com.example.newsappfirebase.repository.RemoteRepository
import kotlin.coroutines.suspendCoroutine

class NewsPagingSource(
    private val remoteRepository: RemoteRepository,
    private val firebaseRepository: FirebaseRepository,
    private val category: String,
    private val query: String? = null
) :
    PagingSource<Int, NewsModel>() {

    override fun getRefreshKey(state: PagingState<Int, NewsModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val closestPage = state.closestPageToPosition(anchorPosition)
        return closestPage?.prevKey?.plus(1) ?: closestPage?.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsModel> {
        val page = params.key ?: 1
        return try {
            val favNews = getFavoritesSuspend()
            val response = remoteRepository.fetchNews(page, category)
            if (response.isSuccessful) {
                val newsResponse = response.body()
                var newsList = newsResponse?.result ?: emptyList()
                if (!query.isNullOrEmpty()) {
                    newsList = newsList.filter {
                        it.name?.contains(query, ignoreCase = true) == true ||
                                it.description?.contains(query, ignoreCase = true) == true
                    }
                }
                val updatedNewsList = newsList.map { news ->
                    val isFavorite = favNews.any { it.name == news.name }
                    news.copy(isFavorite = isFavorite)
                }

                LoadResult.Page(
                    data = updatedNewsList,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (newsList.isEmpty() || newsList.size < PAGINATION_LIMIT) null else page + 1
                )
            } else {
                LoadResult.Error(Exception("API Error: ${response.code()} - ${response.message()}"))
            }
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    private suspend fun getFavoritesSuspend(): List<NewsModel> =
        suspendCoroutine { continuation ->
            firebaseRepository.getFavorites { documents, exception ->
                if (exception != null) {
                    continuation.resumeWith(Result.failure(exception))
                } else {
                    val favoriteNewsList = documents?.mapNotNull { document ->
                        document.toObject(NewsModel::class.java)
                    } ?: emptyList()
                    continuation.resumeWith(Result.success(favoriteNewsList))
                }
            }
        }

    companion object {
        const val PAGINATION_LIMIT = 4
    }
}

