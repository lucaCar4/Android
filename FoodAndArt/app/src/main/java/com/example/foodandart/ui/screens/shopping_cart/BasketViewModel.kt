package com.example.foodandart.ui.screens.shopping_cart

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodandart.data.database.BasketElem
import com.example.foodandart.data.firestore.cloud_database.addPurchase
import com.example.foodandart.data.firestore.cloud_database.getCards
import com.example.foodandart.data.models.BasketActions
import com.example.foodandart.data.models.BasketState
import com.example.foodandart.data.repositories.BasketRepository
import com.example.foodandart.service.dates
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BasketViewModel(private val repository: BasketRepository) : ViewModel() {
    val selectedCards = getCards()
    val limits = mutableStateMapOf<String, MutableMap<String, Int>>()

    val state = repository.basket.map { BasketState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = BasketState(emptyList())
    )

    val actions = object : BasketActions {
        override fun addElem(elem: BasketElem) = viewModelScope.launch {
            repository.upsert(elem)
        }

        override fun removeElem(elem: BasketElem) = viewModelScope.launch {
            repository.delete(elem)
        }

        override fun updateElem(elem: BasketElem) = viewModelScope.launch {
            repository.update(elem)
        }
    }

    fun updateLimit(cardId: String) {
        val newLimits = mutableMapOf<String, Int>()
        dates[cardId]?.documents?.forEach { snapshot ->
            val data = snapshot.data as? Map<String, Any>
            val value1 = data?.get("availability").toString().toInt()
            val value2 =  data?.get("booked").toString().toInt()
            val newLimit = value1 - value2
            newLimits[data?.get("date").toString()] = newLimit
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

}

