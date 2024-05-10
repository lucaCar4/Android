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
import com.example.foodandart.data.firestore.cloud_database.cardsUpdate
import com.example.foodandart.data.firestore.cloud_database.getCards
import com.example.foodandart.data.firestore.cloud_database.getFavorites
import com.example.foodandart.data.firestore.cloud_database.purchasesChangeListener
import com.example.foodandart.data.firestore.cloud_database.userDataChangeListener
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
import org.koin.androidx.compose.get
import java.util.Objects
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class HomeViewModel(private val repository: HomeChipsRepository) : ViewModel() {
    var restaurants by mutableStateOf("false")
    var museums by mutableStateOf("false")
    var packages by mutableStateOf("false")
    var position by mutableStateOf("false")

    var geoPoint by mutableStateOf(GeoPoint(0.0, 0.0))

    var cards = getCards()

    var favorites = getFavorites()

    init {
        viewModelScope.launch {
            restaurants = repository.restaurants.first()
            museums = repository.museums.first()
            packages = repository.packages.first()
            favoritesChangeListener()
            if (accountService.currentUserId != "") {
                userDataChangeListener()
                purchasesChangeListener()
            }
        }
    }

    private var favoritesJob: Job? = null

    private fun favoritesChangeListener() {

    }

    override fun onCleared() {
        super.onCleared()
        favoritesJob?.cancel()
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
                repository.setChip(value, name)
            }
        }

    }

    fun showCardByPosition() : Map<String, Map<String, Any>> {
        val removeList = mutableListOf<String>()
        cards.forEach { (key, value) ->
            val targetCoord = value["coordinates"] as? List<GeoPoint>
            targetCoord?.forEach {
                val distance = calculateDistance(
                    geoPoint.latitude, geoPoint.longitude, it.latitude, it.longitude
                )
                Log.d("Distance", "Per ${value["title"]}, dist = $distance")
                if (distance > 30) {
                    removeList.add(key)
                }
            }
        }
        return cards.filter { !removeList.contains(it.key) }
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