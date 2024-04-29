package com.example.foodandart.data.firestore.cloud_database

import android.util.Log
import com.example.foodandart.accountService
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await


fun getCardsWithFilters(
    restaurants: Boolean,
    museums: Boolean,
    packages: Boolean,
    position: Boolean
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

fun addUserDocument( name : String, cityGeoPoint : GeoPoint, city : String) {
    val db = Firebase.firestore
    val data = hashMapOf(
        "favorites" to emptyList<String>(),
        "name" to name,
        "cityGeoPoint" to cityGeoPoint,
        "city" to city

    )
    db.collection("users").document(accountService.currentUserId).set(data)
}

suspend fun addRemoveFavoriteCard(cardId : String): MutableList<String> {
    val db = Firebase.firestore
    val favorites = getFavoritesCardsId()
    if (favorites.contains(cardId)) {
        favorites.remove(cardId)
    } else {
        favorites.add(cardId)
    }
    db.collection("users").document(accountService.currentUserId).update("favorites", favorites)
    return favorites
}

suspend fun getFavoritesCardsId(): MutableList<String> {
    val db = Firebase.firestore
    val favorites = mutableListOf<String>()
    db.collection("users").document(accountService.currentUserId).get().addOnSuccessListener {document ->
        document.data?.forEach { item ->
            if (item.key == "favorites") {
                (item.value as? List<*>)?.forEach { fav ->
                    favorites.add(fav.toString())
                }
            }
        }
    }.await()
    return favorites
}

suspend fun getFavoritesCards(): MutableList<DocumentSnapshot> {
    val db = Firebase.firestore
    val favoritesCards = mutableListOf<DocumentSnapshot>()
    val favoritesId = getFavoritesCardsId()
    for (id in favoritesId) {
        try {
            val document = db.collection("cards").document(id).get().await()
            favoritesCards.add(document)
        } catch (_: Exception) {
        }
    }
    Log.d("Cards", "FInal size ${favoritesCards.size}")
    return favoritesCards
}

suspend fun userInfo(): DocumentSnapshot? {
    val db = Firebase.firestore
    return db.collection("users").document(accountService.currentUserId).get().await()
}

