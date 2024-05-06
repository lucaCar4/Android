package com.example.foodandart.ui.screens.purchases

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodandart.data.firestore.cloud_database.getPurchases
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Locale

class PurchasesViewModel : ViewModel() {
    var purchases = emptyList<DocumentSnapshot>()
    val selectedCards = mutableStateMapOf<String, Map<String, Any>>()

    var showPermissionDeniedAlert by mutableStateOf(false)
    var showPermissionPermanentlyDeniedSnackBar by mutableStateOf(false)

    private var cardsUpdate: Job? = null
    init {
        viewModelScope.launch {
            purchases = getPurchases()?.documents ?: emptyList()
        }
        cardsChangeListener()
    }

    private fun cardsChangeListener() {
        cardsUpdate = CoroutineScope(Dispatchers.IO).launch {
            Log.d("Basket", "Entro in job")
            val db = Firebase.firestore
            val collection = "cards" + Locale.getDefault().language
            val docRef = db.collection(collection)
            docRef.addSnapshotListener { snapshot, _ ->
                snapshot?.documents?.forEach { document ->
                    val docId = document.id
                    document.reference.addSnapshotListener { docSnapshot, _ ->
                        if (docSnapshot != null && docSnapshot.exists()) {
                            val data = docSnapshot.data as? Map<String, Any>
                            if (data != null) {
                                selectedCards[docId] = data
                            }
                        }
                    }
                }
            }
        }
    }
}