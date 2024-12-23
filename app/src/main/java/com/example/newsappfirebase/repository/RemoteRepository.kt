package com.example.newsappfirebase.repository

import com.example.newsappfirebase.model.NewsResponseModel
import com.example.newsappfirebase.network.ApiService
import retrofit2.Response
import javax.inject.Inject

class RemoteRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun fetchNews(page: Int, category: String): Response<NewsResponseModel> {
        return  apiService.getNewsList(paging = page, tag = category)
    }
}