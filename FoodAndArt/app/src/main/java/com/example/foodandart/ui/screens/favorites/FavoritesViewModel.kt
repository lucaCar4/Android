package com.example.foodandart.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodandart.data.firestore.cloud_database.addRemoveFavoriteCard
import com.example.foodandart.data.firestore.cloud_database.getCards
import com.example.foodandart.data.firestore.cloud_database.getFavorites
import kotlinx.coroutines.launch

class FavoritesViewModel : ViewModel() {
    var favorites = getFavorites()
    var cards = getCards()


    fun removeFavorites(cardId: String) {
        viewModelScope.launch {
            addRemoveFavoriteCard(cardId)
        }
    }

}