package com.example.newsappfirebase.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsappfirebase.model.NewsModel

@Dao
interface NewsDao {

    @Query("SELECT * FROM dbNews ORDER BY addedAt DESC")
    suspend fun getAllFavoriteNews(): List<NewsModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favNews: NewsModel)

    @Query("DELETE FROM dbNews WHERE name = :name")
    suspend fun removeFavorite(name: String)


}