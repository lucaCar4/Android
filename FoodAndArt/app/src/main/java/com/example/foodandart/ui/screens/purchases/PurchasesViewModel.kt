package com.example.foodandart.ui.screens.purchases

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.foodandart.accountService
import com.example.foodandart.data.firestore.cloud_database.getCards
import com.example.foodandart.data.firestore.cloud_database.getPurchases
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PurchasesViewModel : ViewModel() {
    var purchases = getPurchases()
    val selectedCards = getCards()

    var showPermissionDeniedAlert by mutableStateOf(false)
    var showPermissionPermanentlyDeniedSnackBar by mutableStateOf(false)

}