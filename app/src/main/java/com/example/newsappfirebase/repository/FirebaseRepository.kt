package com.example.newsappfirebase.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FirebaseRepository() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var lastVisibleDocument: DocumentSnapshot? = null

    fun getFavorites(onResult: (List<DocumentSnapshot>?, Exception?) -> Unit) {
        var query: Query? = null
        if (lastVisibleDocument != null){
            query = firestore.collection("FavoriteNews")
                .startAfter(lastVisibleDocument)
                .limit(10)
        }
        else{
            query = firestore.collection("FavoriteNews")
                .limit(10)
        }
        query.get()
            .addOnSuccessListener { result ->
                lastVisibleDocument = result.documents[result.size() - 1]
                onResult(result.documents, null)
            }
            .addOnFailureListener { exception ->
                onResult(null, exception)
            }
    }


    /*fun addToFavorites(newsId: String, newsData: Map<String, String?>) =
        favoritesCollection.document(newsId).set(newsData)

    fun removeFromFavorites(newsId: String) =
        favoritesCollection.document(newsId).delete()*/
}