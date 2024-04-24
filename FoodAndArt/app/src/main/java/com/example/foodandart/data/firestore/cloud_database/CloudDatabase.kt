package com.example.foodandart.data.firestore.cloud_database

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await


fun getCardsWithFilters(restaurants : Boolean, museums : Boolean, packages : Boolean, position : Boolean) : MutableList<Query> {
    val db = Firebase.firestore
    val query = mutableListOf<Query>()
    if (restaurants) {
        query.add(db.collection("cards").whereEqualTo("type", "Restaurant"))
    }
    if (museums) {
        query.add(db.collection("cards").whereEqualTo("type", "Museum"))
        Log.d("Cards","museums")
    }
    if (packages) {
        query.add(db.collection("cards").whereEqualTo("type", "Package"))
    }
    if (query.isEmpty()) {
        query.add(db.collection("cards"))
    }
    return query
}