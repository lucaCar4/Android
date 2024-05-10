package com.example.foodandart.ui.screens.cardDetails

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodandart.data.database.BasketElem
import com.example.foodandart.data.firestore.cloud_database.getCardById
import com.example.foodandart.data.models.BasketActions
import com.example.foodandart.data.models.BasketState
import com.example.foodandart.data.repositories.BasketRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CardDetailsViewModel(private val repository: BasketRepository) : ViewModel() {

    var document by mutableStateOf<Map<String, Any>?>(null)

    var dates = mutableStateMapOf<String, Map<String, Any>>()

    var availableDates = mutableStateMapOf<String, Map<String, Any>>()

    var id by mutableStateOf("")
    var showMap by mutableStateOf(false)
    var selectedDate by mutableStateOf("")
    var quantity by mutableIntStateOf(0)
    var limit by mutableIntStateOf(0)

    private var datesUpdate: Job? = null

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

    private fun datesChangeListener(cardId: String) {
        datesUpdate = CoroutineScope(Dispatchers.IO).launch {
            Log.d("Datess", "L'id è $cardId")
            val db = Firebase.firestore
            db.collection("cards"+ Locale.current.language)
                .document(cardId)
                .collection("dates")
                .addSnapshotListener { snapshot, _ ->
                    snapshot?.documents?.forEach { document ->
                        val docId = document.id
                        document.reference.addSnapshotListener { docSnapshot, _ ->
                            if (docSnapshot != null && docSnapshot.exists()) {
                                Log.d("Dates", "Entro")
                                dates[docId] = docSnapshot.data as Map<String, Any>
                            }
                        }
                    }
                }
        }
    }

    fun setDocument(cardId: String) {
        viewModelScope.launch {
            document = getCardById(cardId)
            id = cardId
            datesChangeListener(cardId)
            updateDates()
        }
    }

    private fun updateDates() {
        Log.d("Dates", "Entro in update")
        if (dates.isNotEmpty()) {
            var newDates = dates.filter { (key, data) ->
                LocalDate.parse(
                    data["date"].toString(),
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                ) >= LocalDate.now() && data["availability"].toString().toInt() > data["booked"].toString().toInt()
            }
            Log.d("Dates", newDates.values.toString())
            newDates = newDates.entries.sortedBy {
                LocalDate.parse(
                    it.value["date"].toString(),
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
            }.associate { it.key to it.value }
            Log.d("Dates", newDates.values.toString())
            if (newDates.isNotEmpty()) {
                selectedDate = newDates.values.first()["date"].toString()
                availableDates.clear()
                availableDates.putAll(newDates)
            }
        }
    }

    fun getLimit() {
        val values = availableDates.values
        Log.d("Datess", values.toString())
        values.forEach {
            if (it["date"].toString() == selectedDate) {
                limit = it["availability"].toString().toInt() -it["booked"].toString().toInt()
                Log.d("Datess", limit.toString())
                if (quantity > limit) {
                    quantity = limit
                }
            }
        }
    }
}
