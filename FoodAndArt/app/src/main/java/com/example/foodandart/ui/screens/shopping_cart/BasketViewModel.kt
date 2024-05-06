package com.example.foodandart.ui.screens.shopping_cart

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodandart.data.database.BasketElem
import com.example.foodandart.data.firestore.cloud_database.addPurchase
import com.example.foodandart.data.models.BasketActions
import com.example.foodandart.data.models.BasketState
import com.example.foodandart.data.repositories.BasketRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class BasketViewModel(private val repository: BasketRepository) : ViewModel() {
    val selectedCards = mutableStateMapOf<String, Map<String, Any>>()
    val limits = mutableStateMapOf<String, MutableMap<String, Int>>()

    val state = repository.basket.map { BasketState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = BasketState(emptyList())
    )

    val actions = object : BasketActions {
        override fun addElem(elem : BasketElem) = viewModelScope.launch {
            repository.upsert(elem)
        }
        override fun removeElem(elem : BasketElem) = viewModelScope.launch {
            repository.delete(elem)
        }

        override fun updateElem(elem: BasketElem) = viewModelScope.launch {
            repository.update(elem)
        }
    }

    private var cardsUpdate: Job? = null
    init {
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
                                updateLimit(docId)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateLimit(cardId: String) {
        val newLimits = mutableMapOf<String, Int>()
        (selectedCards[cardId]?.get("dates") as? Map<String, String>)?.forEach { (key, value) ->
            val values = value.split('/')
            if (values.size == 2) {
                val newLimit = values[1].toInt() - values[0].toInt()
                newLimits[key] = newLimit
            }
        }
        limits[cardId] = newLimits
    }

    fun buy(state: BasketState) {
        viewModelScope.launch {
            addPurchase(state)
        }
        state.basket.forEach {
            actions.removeElem(it)
        }
    }

    override fun onCleared() {
        super.onCleared()
        cardsUpdate?.cancel()
    }

}

