package com.example.newsappfirebase.repository

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepository() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private fun favoritesCollection(email: String) =
        firestore.collection("users").document(email).collection("favorites")

    fun getFavoritesByEmail(
        email: String,
        onResult: (List<DocumentSnapshot>?, Exception?) -> Unit
    ) {
        favoritesCollection(email).get()
            .addOnSuccessListener { result ->
                onResult(result.documents, null)
            }
            .addOnFailureListener { exception ->
                onResult(null, exception)
            }
    }

     fun initializeUserDocument(email: String, onResult: (Boolean, Exception?) -> Unit) {
        val userDoc = FirebaseFirestore.getInstance()
            .collection("users")
            .document(email)

        val userData = hashMapOf(
            "email" to email
        )

        userDoc.set(userData)
            .addOnSuccessListener {
                createFavoritesCollection(email)
                onResult(true, null)
            }
            .addOnFailureListener { exception ->
                onResult(false, exception)
            }
    }


    fun addToFavorites(email: String, name: String, newsData: Map<String, String?>) {
        val userDocument = firestore.collection("users").document(email)

        userDocument.collection("favorites").document(name).set(newsData)
            .addOnSuccessListener {
                Log.d("Firebase", "Haber favorilere eklendi")
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Hata oluştu: ${exception.message}")
            }
    }

    private fun createFavoritesCollection(email: String) {
        val favoritesDoc = FirebaseFirestore.getInstance()
            .collection("users")
            .document(email)
            .collection("favorites")
            .document()

        val defaultData = hashMapOf(
            "status" to "initialized"
        )

        favoritesDoc.set(defaultData)
            .addOnSuccessListener {
                Log.d("Firestore", "Favorites collection initialized for $email")
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error initializing favorites collection: ", exception)
            }
    }

    fun removeFromFavorites(
        email: String,
        name: String
    ) {
        favoritesCollection(email).document(name).delete()
    }
}