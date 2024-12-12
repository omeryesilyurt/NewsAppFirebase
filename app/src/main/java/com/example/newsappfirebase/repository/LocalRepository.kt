package com.example.newsappfirebase.repository

import android.content.Context
import com.example.newsappfirebase.database.NewsDatabase
import com.example.newsappfirebase.model.NewsModel

class LocalRepository(context: Context) {
    private val newsDB = NewsDatabase.getInstance(context)

    suspend fun add(news: NewsModel) {
        newsDB?.NewsDao()?.addFavorite(news)
    }

    suspend fun remove(news: NewsModel) {
        news.name?.let { newsDB?.NewsDao()?.removeFavorite(it) }
    }

    suspend fun getFavoriteList(): List<NewsModel>? {
        return newsDB?.NewsDao()?.getAllFavoriteNews()
    }

}