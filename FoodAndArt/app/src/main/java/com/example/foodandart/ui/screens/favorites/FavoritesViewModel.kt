package com.example.foodandart.ui.screens.favorites

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodandart.accountService
import com.example.foodandart.data.firestore.cloud_database.addRemoveFavoriteCard
import com.example.foodandart.data.firestore.cloud_database.getCardsWithFilters
import com.example.foodandart.data.firestore.cloud_database.getFavoritesCards
import com.example.foodandart.data.repositories.HomeChipsRepositories
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FavoritesViewModel () : ViewModel(){
    var favorites = mutableStateListOf<DocumentSnapshot>()

    private var favoritesJob: Job? = null
    init {
        viewModelScope.launch {
            favorites.clear()
            favoritesChangeListener()
        }
    }

    private fun favoritesChangeListener() {
        favoritesJob = CoroutineScope(Dispatchers.IO).launch {
            val db = Firebase.firestore
            db.collection("users").document(accountService.currentUserId).addSnapshotListener { snapshot, e ->
                if (snapshot != null && snapshot.data != null) {
                    Log.d("Favorites", "UpdateVav")
                    val fav = snapshot.data?.get("favorites") as? List<String>
                    if (fav != null && favorites != fav) {
                        viewModelScope.launch {
                            val newFavoritesCards = getFavoritesCards(fav)
                            Log.d("Favorites", newFavoritesCards.toString())
                            favorites.addAll(newFavoritesCards.subtract(favorites))
                            favorites.retainAll(newFavoritesCards)
                        }
                    }
                }
            }
        }
    }

    fun removeFavorites(cardId :String) {
        viewModelScope.launch {
            addRemoveFavoriteCard(cardId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        favoritesJob?.cancel()
    }
}