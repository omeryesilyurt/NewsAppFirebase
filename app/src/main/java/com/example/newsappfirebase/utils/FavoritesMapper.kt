package com.example.newsappfirebase.utils

import com.example.newsappfirebase.model.NewsModel

object FavoritesMapper {

    fun toMap(news: NewsModel, userEmail: String?): Map<String, String?> {
        return mapOf(
            "id" to news.id,
            "name" to news.name,
            "description" to news.description,
            "image" to news.image,
            "source" to news.source,
            "url" to news.url,
            "email" to userEmail
        )
    }
}