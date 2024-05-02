package com.example.foodandart.data.firestore.cloud_database

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.foodandart.accountService
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
fun getCardsWithFilters(
    restaurants: Boolean,
    museums: Boolean,
    packages: Boolean,
): MutableList<Query> {
    val db = Firebase.firestore

    val query = mutableListOf<Query>()
    if (restaurants) {
        query.add(db.collection("cards").whereEqualTo("type", "Restaurant"))
    }
    if (museums) {
        query.add(db.collection("cards").whereEqualTo("type", "Museum"))
        Log.d("Cards", "museums")
    }
    if (packages) {
        query.add(db.collection("cards").whereEqualTo("type", "Package"))
    }
    if (query.isEmpty()) {
        query.add(db.collection("cards"))
    }
    return query
}

fun removeUserFiles() {
    val db = Firebase.firestore
    db.collection("users").document(accountService.currentUserId).delete()
}

fun addUserDocument(name: String, cityGeoPoint: GeoPoint, city: String) {
    val db = Firebase.firestore
    val data = hashMapOf(
        "favorites" to emptyList<String>(),
        "name" to name,
        "cityGeoPoint" to cityGeoPoint,
        "city" to city

    )
    db.collection("users").document(accountService.currentUserId).set(data)
}

suspend fun addRemoveFavoriteCard(cardId: String) {
    val db = Firebase.firestore
    db.collection("users").document(accountService.currentUserId).get().addOnSuccessListener {
        val fav = it.data?.get("favorites") as? MutableList<String>
        if (fav != null) {
            if (fav.contains(cardId)) {
                fav.remove(cardId)
            } else {
                fav.add(cardId)
            }
            db.collection("users").document(accountService.currentUserId)
                .update("favorites", fav)
        }
    }.await()
}



suspend fun getFavoritesCards(favoritesId : List<String>): MutableList<DocumentSnapshot> {
    val db = Firebase.firestore
    val favoritesCards = mutableListOf<DocumentSnapshot>()
    for (id in favoritesId) {
        try {
            val document = db.collection("cards").document(id).get().await()
            favoritesCards.add(document)
        } catch (_: Exception) {
        }
    }
    return favoritesCards
}

suspend fun userInfo(): DocumentSnapshot? {
    val db = Firebase.firestore
    return db.collection("users").document(accountService.currentUserId).get().await()
}

suspend fun getCardById(cardId: String): DocumentSnapshot? {
    val db = Firebase.firestore
    return db.collection("cards").document(cardId).get().await()
}

