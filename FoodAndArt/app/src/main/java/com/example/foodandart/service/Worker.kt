package com.example.foodandart.service

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.text.intl.Locale
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.foodandart.accountService
import com.example.foodandart.data.firestore.cloud_database.getCards
import com.example.foodandart.notificationService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

var dates = mutableStateMapOf<String, QuerySnapshot>()

var favorites = mutableStateListOf<String>()

class DateUpdateWorker(context: Context, params: WorkerParameters) : Worker(context, params)  {
    val cards = getCards()
    override fun doWork(): Result {
        val db = FirebaseFirestore.getInstance()

        if (accountService.currentUserId != "") {
            val userDocumentRef = db.collection("users").document(accountService.currentUserId)
            userDocumentRef.get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val fav = snapshot.data?.get("favorites") as? List<String>
                        fav?.let {
                            if (favorites != it) {
                                val newElements = it.subtract(favorites)
                                val removedElements = favorites.filter { !it.contains(it) }
                                favorites.addAll(newElements)
                                favorites.retainAll(it)
                                updateListeners(db, removedElements)
                            }
                        }
                    }
                }
                .addOnFailureListener { _ ->
                }
        }
        return Result.success()
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