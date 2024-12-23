package com.example.newsappfirebase.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepository() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val favoritesCollection = FirebaseFirestore.getInstance().collection("FavoriteNews")

    fun getFavorites(onResult: (List<DocumentSnapshot>?, Exception?) -> Unit) {
        firestore.collection("FavoriteNews")
            .get()
            .addOnSuccessListener { result ->
                onResult(result.documents, null)
            }
            .addOnFailureListener { exception ->
                onResult(null, exception)
            }
    }


    fun addToFavorites(newsId: String, newsData: Map<String, String?>) =
        favoritesCollection.document(newsId).set(newsData)

    fun removeFromFavorites(newsId: String) =
        favoritesCollection.document(newsId).delete()
}