package com.example.newsappfirebase.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsappfirebase.model.NewsModel
import com.example.newsappfirebase.network.ApiService
import com.example.newsappfirebase.repository.FirebaseRepository
import kotlinx.coroutines.tasks.await
import java.util.UUID

class NewsPagingSource(
    private val firebaseRepository: FirebaseRepository,
    private val apiService: ApiService,
    private val category: String,
    private val query: String? = null,
    private val isFavoritesMode: Boolean = false
) :
    PagingSource<Int, NewsModel>() {
    override fun getRefreshKey(state: PagingState<Int, NewsModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val closestPage = state.closestPageToPosition(anchorPosition)
        return closestPage?.prevKey?.plus(1) ?: closestPage?.nextKey?.minus(1)
    }


    // TODO HABERLER FAVORİ OLARAK KAYDEDİLİYOR AMA FAVORİ OLARAK LİSTELENMİYOR
    // TODO PAGINGSOURCE KISMINDA FAVORI GET ISLEMI KONTROL EDILMELI
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsModel> {
        val page = params.key ?: 1
        return try {
            val newsList: List<NewsModel> = if (isFavoritesMode) {
                fetchFavoritesFromFirebase()
            } else {
                val response = apiService.getNewsList(tag = category, paging = page)
                val fetchedList = response.body()?.result ?: emptyList()

                if (!query.isNullOrEmpty()) {
                    fetchedList.filter { it.name?.contains(query, ignoreCase = true) == true }
                } else {
                    fetchedList
                }
            }

            val favoriteNews = fetchFavoritesFromFirebase()
            newsList.forEach { news ->
                news.isFavorite = favoriteNews.any { it.name == news.name }
            }
            LoadResult.Page(
                data = newsList,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (newsList.isEmpty() || newsList.size < PAGINATION_LIMIT) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun fetchFavoritesFromFirebase(): List<NewsModel> {
        return try {
            val favoriteDocuments = firebaseRepository.getFavorites().await()
            favoriteDocuments.mapNotNull { document ->
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
    }

    companion object {
        const val PAGINATION_LIMIT = 4
    }
}

