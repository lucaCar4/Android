package com.example.foodandart.ui.screens.CardDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodandart.data.firestore.cloud_database.getCardById
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch

class CardDetailsViewModel : ViewModel() {
    
    var document by mutableStateOf<DocumentSnapshot?>(null)
    fun getDocubentById(cardId:String) {
        viewModelScope.launch {
            document = getCardById(cardId)
        }
    }

}