package com.example.foodandart.ui.screens.cardDetails

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodandart.data.database.BasketElem
import com.example.foodandart.data.firestore.cloud_database.getCardById
import com.example.foodandart.data.models.BasketActions
import com.example.foodandart.data.models.BasketState
import com.example.foodandart.data.repositories.BasketRepository
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Objects

class CardDetailsViewModel(private val repository: BasketRepository) : ViewModel() {

    var document by mutableStateOf<Map<String, Any>?>(null)
    var id by mutableStateOf("")
    var showMap by mutableStateOf(false)
    var selectedDate by mutableStateOf("")
    var dates = mutableStateMapOf<String, String>()
    var quantity by mutableIntStateOf(0)
    var limit by mutableIntStateOf(0)

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

        override fun updateElem(elem: BasketElem): Job {
            TODO("Not yet implemented")
        }
    }

    fun setDocument(cardId: String) {
        viewModelScope.launch {
            document = getCardById(cardId)
            id = cardId
            updateDates()
        }
    }

    private fun updateDates() {
        var docDates = document?.get("dates") as? Map<String, String>
        if (!docDates.isNullOrEmpty()) {
            Log.d("Data", docDates.toString())
            docDates = docDates.filterKeys {
                LocalDate.parse(
                    it,
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                ) >= LocalDate.now()
            }
            Log.d("Data", docDates.toString())
            docDates = docDates.entries.sortedBy {
                LocalDate.parse(
                    it.key,
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
            }.associate { it.key to it.value }
            if (docDates.isNotEmpty()) {
                selectedDate = docDates.keys.first()
                docDates.forEach { (key, value) ->
                    dates[key] = value
                }

            }
        }
    }

    fun getLimit() {
        val values = dates[selectedDate]?.split('/')
        if (values != null && values.size == 2) {
            limit = values[1].toInt() - values[0].toInt()
            if (quantity > limit) {
                quantity = limit
            }
        }
    }
}