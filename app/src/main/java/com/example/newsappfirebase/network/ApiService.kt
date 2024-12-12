package com.example.newsappfirebase.network

import com.example.newsappfirebase.model.NewsResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("getNews?country=tr")
    suspend fun getNewsList(
        @Query("tag") tag: String,
        @Query("paging") paging: Int
    ): Response<NewsResponseModel>
}