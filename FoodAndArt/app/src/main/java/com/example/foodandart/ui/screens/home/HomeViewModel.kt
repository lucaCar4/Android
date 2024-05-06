package com.example.foodandart.ui.screens.home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodandart.accountService
import com.example.foodandart.data.firestore.cloud_database.addRemoveFavoriteCard
import com.example.foodandart.data.firestore.cloud_database.getCardsWithFilters
import com.example.foodandart.data.repositories.HomeChipsRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class HomeViewModel(private val repository: HomeChipsRepository) : ViewModel() {
    private var restaurants by mutableStateOf("false")
    private var museums by mutableStateOf("false")
    private var packages by mutableStateOf("false")
    var position by mutableStateOf("false")
        private set

    var geoPoint by mutableStateOf(GeoPoint(0.0, 0.0))

    var docs = mutableStateMapOf<String, Map<String, Object>>()

    var favorites = mutableStateListOf<String>()

    init {
        viewModelScope.launch {
            restaurants = repository.restaurants.first()
            museums = repository.museums.first()
            packages = repository.packages.first()
            favoritesChangeListener()
            cardsChangeListener()
            updateCards()
        }
    }

    private var cardsJob: Job? = null
    private var favoritesJob: Job? = null

    private fun cardsChangeListener() {
        cardsJob = CoroutineScope(Dispatchers.IO).launch {
            val db = Firebase.firestore
            val docRef = db.collection("cards")
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                snapshot?.documents?.forEach { document ->
                    val docId = document.id
                    document.reference.addSnapshotListener { docSnapshot, docError ->
                        if (docSnapshot != null && docSnapshot.exists()) {
                            docs[docId] = docSnapshot.data as Map<String, Object>
                            Log.d("Cards", "Document $docId changed: ${docSnapshot.data}")
                        }
                    }
                }
            }
        }
    }

    private fun favoritesChangeListener() {
        favoritesJob = CoroutineScope(Dispatchers.IO).launch {
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
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        cardsJob?.cancel()
        favoritesJob?.cancel()
    }

    private suspend fun updateCards() {
        val newDocs: MutableMap<String, Map<String, Object>> = mutableMapOf()
        Log.d("Cards", "Rest $restaurants, Mus $museums, Pack $packages, Pos $position")
        docs.clear()
        val querys =
            getCardsWithFilters(restaurants.toBoolean(), museums.toBoolean(), packages.toBoolean())
        Log.d("Card", "Docs ${querys.size}")
        querys.forEach { query ->
            query.get().addOnSuccessListener { documents ->
                documents.forEach { document ->
                    Log.d("Card", "Document ${document.id}")
                    docs[document.id] = document.data as Map<String, Object>
                }
            }.await()
        }
        Log.d("Cards", "Docs is $newDocs")
    }

    fun setChip(value: String, name: String) {
        Log.d("Chip", "Setto $name a $value")
        when (name) {
            "restaurants" -> restaurants = value
            "museums" -> museums = value
            "packages" -> packages = value
            "position" -> position = value
        }
        viewModelScope.launch {
            if (name != "position" || position == "false") {
                updateCards()
                repository.setChip(value, name)
            }
            Log.d("Cards", "Fine chip")
        }

    }

    fun showCardByPosition() {
        val removeList = mutableListOf<String>()
        docs.forEach { (key, value) ->
            val targetCoord = value["coordinates"] as? List<GeoPoint>
            targetCoord?.forEach {
                val distance = calculateDistance(
                    geoPoint.latitude, geoPoint.longitude, it.latitude, it.longitude
                )
                if (distance > 30) {
                    removeList.add(key)
                }
            }
        }
        removeList.forEach { key -> docs.remove(key) }
    }

    private fun calculateDistance(
        myLatitude: Double, myLongitude: Double, targetLatitude: Double, targetLongitude: Double
    ): Double {

        val earthRadius = 6371 // Raggio medio della Terra in chilometri

        val dLat = Math.toRadians(targetLatitude - myLatitude)
        val dLon = Math.toRadians(targetLongitude - myLongitude)

        val a = sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(myLatitude)) * cos(
            Math.toRadians(targetLatitude)
        ) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    fun getVar(name: String): String {
        return when (name.lowercase()) {
            "restaurants" -> restaurants
            "museums" -> museums
            "packages" -> packages
            "position" -> position
            else -> ""
        }
    }


    fun addFavorites(cardId: String) {
        viewModelScope.launch {
            addRemoveFavoriteCard(cardId)
        }
    }
}