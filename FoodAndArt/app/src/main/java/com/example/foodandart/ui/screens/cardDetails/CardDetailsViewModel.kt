package com.example.foodandart.ui.screens.cardDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodandart.data.firestore.cloud_database.getCardById
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CardDetailsViewModel : ViewModel() {

    var document by mutableStateOf<DocumentSnapshot?>(null)
    var showMap by mutableStateOf(false)
    var selectedDate by mutableStateOf("")
    var dates = mutableStateMapOf<String, String>()
    var quantity by mutableIntStateOf(0)
    var limit by mutableIntStateOf(0)

    fun setDocument(cardId: String) {
        viewModelScope.launch {
            document = getCardById(cardId)
            updateDates()
        }
    }

    private fun updateDates() {
        var docDates = document?.data?.get("dates") as? Map<String, String>
        if (!docDates.isNullOrEmpty()) {
            docDates = docDates.filterKeys {
                LocalDate.parse(
                    it,
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                ) >= LocalDate.now()
            }
            docDates = docDates.entries.sortedBy {
                LocalDate.parse(
                    it.key,
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
            }
                .associate { it.key to it.value }
            selectedDate = docDates.keys.first()
            docDates.forEach { (key, value) ->
                dates[key] = value
            }
            getLimit()
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