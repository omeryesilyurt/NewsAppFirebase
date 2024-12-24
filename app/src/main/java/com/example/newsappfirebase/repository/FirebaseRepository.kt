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

    fun getFavoritesByEmail(email: String, onResult: (List<DocumentSnapshot>?, Exception?) -> Unit) {
        favoritesCollection.whereEqualTo("email", email).get()
            .addOnSuccessListener { result ->
                onResult(result.documents, null)
            }
            .addOnFailureListener { exception ->
                onResult(null, exception)
            }
    }



    fun addToFavorites(id: String, newsData: Map<String, String?>) =
        favoritesCollection.document(id).set(newsData)

    fun removeFromFavorites(id: String) =
        favoritesCollection.document(id).delete()
}