package com.example.newsappfirebase.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsappfirebase.model.NewsModel
import com.example.newsappfirebase.network.ApiService
import com.example.newsappfirebase.repository.LocalRepository

class NewsPagingSource(
    private val apiService: ApiService,
    private val category: String,
    private val localRepository: LocalRepository,
    private val query: String? = null,
    private val isFavoritesMode: Boolean = false
) :
    PagingSource<Int, NewsModel>() {
    override fun getRefreshKey(state: PagingState<Int, NewsModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val closestPage = state.closestPageToPosition(anchorPosition)
        return closestPage?.prevKey?.plus(1) ?: closestPage?.nextKey?.minus(1)
    }
    private var hasLoadedFavorites = false

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsModel> {
        val page = params.key ?: 1
        return try {
            val newsList: List<NewsModel> = if (isFavoritesMode && !hasLoadedFavorites) {
                hasLoadedFavorites = true
                localRepository.getFavoriteList() ?: emptyList()
            } else {
                val response = apiService.getNewsList(tag = category, paging = page)
                response.body()?.result ?: emptyList()
                val fetchedList = response.body()?.result ?: emptyList()

                if (!query.isNullOrEmpty()) {
                    fetchedList.filter { it.name?.contains(query, ignoreCase = true) == true }
                } else {
                    fetchedList
                }
            }

            val favoriteNews = localRepository.getFavoriteList()
            newsList.forEach { news ->
                news.isFavorite = favoriteNews?.any { it.name == news.name } == true
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


    companion object {
        const val PAGINATION_LIMIT = 4
    }
}

