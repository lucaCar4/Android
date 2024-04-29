package com.example.foodandart.ui.screens.favorites

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodandart.data.firestore.cloud_database.addRemoveFavoriteCard
import com.example.foodandart.data.firestore.cloud_database.getCardsWithFilters
import com.example.foodandart.data.firestore.cloud_database.getFavoritesCards
import com.example.foodandart.data.firestore.cloud_database.getFavoritesCardsId
import com.example.foodandart.data.repositories.HomeChipsRepositories
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FavoritesViewModel () : ViewModel(){
    var favoritesCard = mutableStateListOf<DocumentSnapshot>()
    init {
        viewModelScope.launch {
            favoritesCard.clear()
            Log.d("Card", "Entro")
            getFavoritesCards().forEach {
                favoritesCard.add(it)
            }
            Log.d("Card", favoritesCard.size.toString())
        }
    }

    fun removeFavorites(cardId :String) {
        viewModelScope.launch {
            favoritesCard.removeIf {it.id == cardId}
            addRemoveFavoriteCard(cardId)
        }
    }
}