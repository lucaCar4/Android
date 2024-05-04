package com.example.foodandart.data.firestore.cloud_database

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
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
    val collection = "cards" + Locale.current.language
    val query = mutableListOf<Query>()
    if (restaurants) {
        query.add(db.collection(collection).whereEqualTo("type", "Restaurant"))
    }
    if (museums) {
        query.add(db.collection(collection).whereEqualTo("type", "Museum"))
        Log.d(collection, "museums")
    }
    if (packages) {
        query.add(db.collection(collection).whereEqualTo("type", "Package"))
    }
    if (query.isEmpty()) {
        query.add(db.collection(collection))
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
    val collection = "cards" + Locale.current.language
    val favoritesCards = mutableListOf<DocumentSnapshot>()
    for (id in favoritesId) {
        try {
            val document = db.collection(collection).document(id).get().await()
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
    val collection = "cards" + Locale.current.language
    return db.collection(collection).document(cardId).get().await()
}

