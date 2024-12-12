package com.example.newsappfirebase.model

import com.google.gson.annotations.SerializedName

data class NewsResponseModel (
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("result")
    val result: List<NewsModel>
)
