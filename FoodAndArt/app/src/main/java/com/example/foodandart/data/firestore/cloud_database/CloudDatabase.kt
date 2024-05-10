package com.example.foodandart.data.firestore.cloud_database

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.intl.Locale
import com.example.foodandart.accountService
import com.example.foodandart.data.models.BasketState
import com.example.foodandart.languages
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.util.Objects


val cards = mutableStateMapOf<String, Map<String, Any>>()
var favorites = mutableStateListOf<String>()
var purchases = mutableStateListOf<Map<String, Any>>()
var userData by mutableStateOf<DocumentSnapshot?>(null)

private var cardsUpdate: Job? = null
private var userUpdate: Job? = null
private var purchasesUpdate: Job? = null


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

fun userInfo(): DocumentSnapshot? {
    return userData
}

fun getCardById(cardId: String): Map<String, Any>? {
    return cards[cardId]
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
        languages.forEach { it ->
            val document = db.collection("cards$it").document(elem.card).collection("dates").get().await()
            document.documents.forEach {document ->
                if (document.data?.get("date").toString() == elem.date) {
                    val oldLimit = document.data?.get("booked").toString().toInt()
                    db.collection("cards$it").document(elem.card).collection("dates").document(document.id).update("booked", elem.quantity+oldLimit).await()
                }
            }

        }
        if (accountService.currentUserId != "") {
            db.collection("users").document(accountService.currentUserId).collection("purchases")
                .add(data).await()
        }
    }
}

fun purchasesChangeListener() {
    purchasesUpdate = CoroutineScope(Dispatchers.IO).launch {
        val db = Firebase.firestore
        if (accountService.currentUserId != "") {
            db.collection("users").document(accountService.currentUserId)
                .collection("purchases")
                .addSnapshotListener { snapshot, _ ->
                    purchases.clear()
                    snapshot?.documents?.forEach { document ->
                        val data = document.data as? Map<String, Any>
                        if (data != null) {
                            purchases.add(data)
                        }
                    }
                }
        }
    }
}


fun userDataChangeListener() {
    userUpdate = CoroutineScope(Dispatchers.IO).launch {
        val db = Firebase.firestore
        db.collection("users").document(accountService.currentUserId)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null && snapshot.data != null) {
                    Log.d("Favorites", "UpdateVav")
                    val fav = snapshot.data?.get("favorites") as? List<String>
                    if (fav != null && favorites != fav) {
                        favorites.addAll(fav.subtract(favorites))
                        favorites.retainAll(fav)
                    }
                    userData = snapshot
                }
            }
    }
}

fun cardsUpdate() {
    cardsUpdate = CoroutineScope(Dispatchers.IO).launch {
        val db = Firebase.firestore
        val docRef = db.collection("cards" + Locale.current.language)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            snapshot?.documents?.forEach { document ->
                val docId = document.id
                Log.d("MainViewModel", "dcccc")
                document.reference.addSnapshotListener { docSnapshot, docError ->
                    if (docSnapshot != null && docSnapshot.exists()) {
                        cards[docId] = docSnapshot.data as Map<String, Any>
                    }
                }
            }
        }
        /*
        cards.forEach {card ->
            docRef.document(card.key).collection("dates").addSnapshotListener { snapshot, _ ->
                snapshot?.documents?.forEach { document ->
                    val docId = document.id
                    document.reference.addSnapshotListener { docSnapshot, _ ->
                        if (docSnapshot != null && docSnapshot.exists()) {
                            dates[card.key] = docSnapshot.data as Map<String, Any>
                        }
                    }
                }
            }
        }
         */
    }
}

fun getCards(): MutableMap<String, Map<String, Any>> {
    return cards
}

fun getFavorites(): List<String> {
    return favorites
}

fun getPurchases(): List<Map<String, Any>> {
    return purchases
}


