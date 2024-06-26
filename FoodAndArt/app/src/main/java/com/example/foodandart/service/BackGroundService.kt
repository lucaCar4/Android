package com.example.foodandart.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.text.intl.Locale
import com.example.foodandart.accountService
import com.example.foodandart.data.firestore.cloud_database.getCards
import com.example.foodandart.notificationService
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Job

var date = mutableStateMapOf<String, QuerySnapshot>()
val cards = getCards()

class BackGroundService : Service() {
    var favorites = mutableStateListOf<String>()
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val db = Firebase.firestore
        if (accountService.currentUserId != "") {
            db.collection("users").document(accountService.currentUserId)
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null && snapshot.data != null) {
                        val fav = snapshot.data?.get("favorites") as? List<String>
                        if (fav != null && favorites != fav) {
                            val newElement = fav.subtract(favorites)
                            val remove = favorites.filter { !fav.contains(it) }
                            favorites.addAll(newElement)
                            favorites.retainAll(fav)
                            updateListeners(db, remove)
                        }
                    }
                }
        }
        return START_STICKY
    }

    private fun updateListeners(db: FirebaseFirestore, removeElements: List<String>) {
        val activeListeners = mutableMapOf<String, ListenerRegistration>()
        favorites.forEach { cardId ->
            val cardDatesRef = db.collection("cards${Locale.current.language}").document(cardId)
                .collection("dates")
            val listener = cardDatesRef.addSnapshotListener { querySnapshot, _ ->
                querySnapshot?.let { snapshot ->
                    if (!snapshot.isEmpty && dates.isNotEmpty() && dates[cardId] != null && dates[cardId]?.size()!! < snapshot.size()) {
                        notificationService?.showCardUpdateNotification(cardId, cards[cardId]?.get("title").toString())
                    }
                    dates[cardId] = snapshot
                }
            }
            activeListeners[cardId] = listener
        }
        removeElements.forEach {
            Log.d("Backk", "Rimuovo $it")
            dates.remove(it)
            activeListeners[it]?.remove()
            activeListeners.remove(it)
        }
    }

}