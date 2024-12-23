package com.example.newsappfirebase.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsappfirebase.model.NewsModel
import com.example.newsappfirebase.repository.RemoteRepository

class NewsPagingSource(
    private val remoteRepository: RemoteRepository,
    private val category: String
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
            val response = remoteRepository.fetchNews(page, category)
            if (response.isSuccessful) {
                val newsResponse = response.body()
                val newsList = newsResponse?.result ?: emptyList()

                LoadResult.Page(
                    data = newsList,
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

    companion object {
        const val PAGINATION_LIMIT = 4
    }
}

