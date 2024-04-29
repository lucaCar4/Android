package com.example.foodandart.ui.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodandart.data.firestore.cloud_database.addRemoveFavoriteCard
import com.example.foodandart.data.firestore.cloud_database.getCardsWithFilters
import com.example.foodandart.data.firestore.cloud_database.getFavoritesCardsId
import com.example.foodandart.data.repositories.HomeChipsRepositories
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel ( private val repository: HomeChipsRepositories) : ViewModel(){
    var restaurants by mutableStateOf("false")
        private set
    var museums by mutableStateOf("false")
        private set
    var packages by mutableStateOf("false")
        private set
    var position by mutableStateOf("false")
        private set

    var docs = mutableStateListOf<QueryDocumentSnapshot>()

    var favorites = mutableStateListOf<String>()

    init {
        viewModelScope.launch {
            updateFavorites()
            restaurants = repository.restaurants.first()
            museums = repository.museums.first()
            packages = repository.packages.first()
            position = repository.position.first()
            updateCards(restaurants, museums, packages, position )
        }
    }

    fun updateFavorites() {
        viewModelScope.launch {
            favorites.clear()
            val newFavorites = getFavoritesCardsId()
            newFavorites.forEach { card ->
                favorites.add(card)
            }
        }
    }

    private suspend fun updateCards(restaurants : String, museums : String, packages : String, position : String) {
        val querys = getCardsWithFilters(restaurants.toBoolean(), museums.toBoolean(), packages.toBoolean(), position.toBoolean())
        docs.clear()
        querys.forEach { query ->
            query?.get()?.addOnSuccessListener { documents ->
                documents.forEach { document ->
                    docs.add(document)
                    Log.d("Cards", "Aggiungo ${document.id}")
                }
            }?.await()
        }
    }

    fun setChip(value: String, name: String) {
        Log.d("Chip", "Setto $name a $value")
        when(name.lowercase()) {
            "restaurants" ->  restaurants = value
            "museums" -> museums = value
            "packages" -> packages = value
            "position" -> position = value
        }
        viewModelScope.launch {
            updateCards(restaurants, museums, packages, position )
            repository.setChip(value, name)
        }

    }

    fun getVar(name : String) : String {
        return when(name.lowercase()) {
            "restaurants" -> restaurants
            "museums" -> museums
            "packages" -> packages
            "position" -> position
            else -> ""
        }
    }


    fun addFavorites(cardId :String) {
        viewModelScope.launch {
            val newFavorites = addRemoveFavoriteCard(cardId)
            favorites.forEach { card ->
                if(!newFavorites.contains(card))  {
                    favorites.remove(card)
                }
            }
            newFavorites.forEach { card ->
                if(!favorites.contains(card))  {
                    favorites.add(card)
                }
            }
        }
    }
}