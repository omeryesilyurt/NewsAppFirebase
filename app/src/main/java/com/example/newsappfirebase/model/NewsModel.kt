package com.example.newsappfirebase.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.errorprone.annotations.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.UUID

@Entity(tableName = "dbNews",indices = [Index(value = ["newsId"], unique = true)])
data class NewsModel(
    @PrimaryKey
    var newsId: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "id")
    var id: String= "",

    @SerializedName("key")
    @ColumnInfo(name = "key")
    val key: String?= null,

    @SerializedName("url")
    @ColumnInfo(name = "url")
    val url: String?= null,

    @SerializedName("description")
    @ColumnInfo(name = "description")
    val description: String?= null,

    @SerializedName("image")
    @ColumnInfo(name = "image")
    val image: String?= null,

    @SerializedName("name")
    @ColumnInfo(name = "name")
    val name: String?= null,

    @SerializedName("source")
    @ColumnInfo(name = "source")
    val source: String?= null,

    @ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean? = false,

    @SerializedName("addedAt")
    @ColumnInfo(name = "addedAt")
    var addedAt:Long = System.currentTimeMillis()
): Serializable{
    constructor() : this(
        newsId = UUID.randomUUID().toString(),
        id = "",
        key = null,
        url = null,
        description = null,
        image = null,
        name = null,
        source = null,
        isFavorite = false,
        addedAt = System.currentTimeMillis()
    )
}
