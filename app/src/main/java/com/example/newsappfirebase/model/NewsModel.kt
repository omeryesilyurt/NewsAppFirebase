package com.example.newsappfirebase.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.UUID

@Entity(tableName = "dbNews",indices = [Index(value = ["newsId"], unique = true)])
data class NewsModel(
    @PrimaryKey
    var newsId: UUID,

    @ColumnInfo(name = "id")
    var id: String,

    @SerializedName("key")
    @ColumnInfo(name = "key")
    val key: String?,

    @SerializedName("url")
    @ColumnInfo(name = "url")
    val url: String?,

    @SerializedName("description")
    @ColumnInfo(name = "description")
    val description: String?,

    @SerializedName("image")
    @ColumnInfo(name = "image")
    val image: String?,

    @SerializedName("name")
    @ColumnInfo(name = "name")
    val name: String?,

    @SerializedName("source")
    @ColumnInfo(name = "source")
    val source: String?,

    @ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean? = false,

    @SerializedName("addedAt")
    @ColumnInfo(name = "addedAt")
    var addedAt:Long = System.currentTimeMillis()
): Serializable
