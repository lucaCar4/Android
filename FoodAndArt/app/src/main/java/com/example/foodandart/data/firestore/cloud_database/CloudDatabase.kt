package com.example.foodandart.data.firestore.cloud_database

import android.util.Log
import androidx.compose.ui.text.intl.Locale
import com.example.foodandart.accountService
import com.example.foodandart.data.models.BasketState
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

val languages = listOf("it", "en")
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


suspend fun getFavoritesCards(favoritesId: List<String>): MutableList<DocumentSnapshot> {
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
    return if (accountService.currentUserId != "") {
        Log.d("User", "User is ${accountService.currentUserId}")
        db.collection("users").document(accountService.currentUserId).get().await()
    } else {
        null
    }
}

suspend fun getCardById(cardId: String): DocumentSnapshot? {
    val db = Firebase.firestore
    val collection = "cards" + Locale.current.language
    return db.collection(collection).document(cardId).get().await()
}

suspend fun addPurchase(basket: BasketState) {
    basket.basket.forEach { elem ->
        val data = hashMapOf(
            "card" to elem.card,
            "date" to elem.date,
            "quantity" to elem.quantity,
            "purchase_date" to LocalDate.now().toString()
        )
        val db = Firebase.firestore
        languages.forEach {
            val document = db.collection("cards$it").document(elem.card).get().await()
            val dates = document.data?.get("dates") as? MutableMap<String, String>
            if (!dates.isNullOrEmpty()) {
                val limit = dates[elem.date]?.split('/')
                if (limit != null && limit.size == 2) {
                    dates[elem.date] = "${limit[0].toInt() + elem.quantity}/${limit[1]}"
                }
                db.collection("cards$it").document(elem.card).update("dates", dates).await()
            }
        }
        if (accountService.currentUserId != "") {
            db.collection("users").document(accountService.currentUserId).collection("purchases")
                .add(data).await()
        }
    }
}

suspend fun getPurchases(): QuerySnapshot? {
    val db = Firebase.firestore
    if (accountService.currentUserId != "") {
        return db.collection("users").document(accountService.currentUserId).collection("purchases")
            .get().await()
    }
    return null
}

