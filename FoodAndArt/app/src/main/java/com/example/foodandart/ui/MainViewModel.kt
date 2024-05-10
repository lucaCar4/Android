package com.example.foodandart.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.foodandart.data.firestore.cloud_database.cardsUpdate
import com.example.foodandart.data.firestore.cloud_database.purchasesChangeListener
import com.example.foodandart.data.firestore.cloud_database.userDataChangeListener

class MainViewModel : ViewModel() {
    init {
        Log.d("MainViewModel", "Attivooo")
        cardsUpdate()
    }

    override fun onCleared() {
        super.onCleared()

    }
}