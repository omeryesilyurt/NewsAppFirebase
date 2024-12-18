package com.example.newsappfirebase.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val favoritesCollection: CollectionReference = firestore.collection("FavoriteNews")

    private fun getUserFavoritesRef() =
        favoritesCollection.document(auth.currentUser?.uid ?: "unknown").collection("favoriteNews")

    fun getFavorites(): Task<List<DocumentSnapshot>> {
        return getUserFavoritesRef().get().continueWith { task ->
            task.result?.documents ?: emptyList()
        }
    }

    fun addToFavorites(newsId: String, newsData: Map<String, String?>) =
        favoritesCollection.document(newsId).set(newsData)

    fun removeFromFavorites(newsId: String) =
        favoritesCollection.document(newsId).delete()
}