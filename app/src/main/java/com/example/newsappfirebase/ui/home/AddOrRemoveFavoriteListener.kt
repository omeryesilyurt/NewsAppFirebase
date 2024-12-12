package com.example.newsappfirebase.ui.home

import com.example.newsappfirebase.model.NewsModel

interface AddOrRemoveFavoriteListener {

    fun onAddOrRemoveFavorite(news: NewsModel, isAdd: Boolean)
}