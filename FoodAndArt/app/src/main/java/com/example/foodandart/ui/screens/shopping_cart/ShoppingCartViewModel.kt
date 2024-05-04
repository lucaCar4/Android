package com.example.foodandart.ui.screens.shopping_cart

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodandart.data.database.Todo
import com.example.foodandart.data.repositories.TodosRepository
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
import java.util.Locale
data class TodosState(val todos: List<Todo>)

interface TodosActions {
    fun addTodo(todo: Todo) : Job
    fun removeTodo(todo: Todo) : Job
}
class ShoppingCartViewModel(private val repository: TodosRepository) : ViewModel() {
    val selectedCards = mutableStateMapOf<String, Map<String, Any>>()
    val state = repository.todos.map { TodosState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = TodosState(emptyList())
    )

    val actions = object : TodosActions {
        override fun addTodo(todo: Todo) = viewModelScope.launch {
            repository.upsert(todo)
        }
        override fun removeTodo(todo: Todo) = viewModelScope.launch {
            repository.delete(todo)
        }
    }
    var limit by mutableIntStateOf(0)

    private var cardsUpdate: Job? = null

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
                                //getLimit(result)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getLimit(element: Todo) {
        val dates = selectedCards[element.card]?.get("dates") as? Map<String, String>
        if (dates != null && dates[element.date] != null) {
            val values = dates[element.date]?.split('/')
            if (values != null && values.size == 2) {
                limit = values[1].toInt() - values[0].toInt()
                if (element.quantity > limit) {
                }
            }
        } else {
            actions.removeTodo(element)
            limit = 0
        }
    }

    private fun isInBasket(cardId: String): Todo? {
        Log.d("Basket", "Is in basket $state")
        state.value.todos.forEach { elem ->
            Log.d("Basket", "$elem, $cardId")
            if (elem.card == cardId) {

                return elem
            }
        }
        return null
    }

    fun addToBasket(element: Todo) {
        val res = isInBasket(element.card)
        if (res != null) {
            actions.removeTodo(res)
        }
        actions.addTodo(element)
    }

    override fun onCleared() {
        super.onCleared()
        cardsUpdate?.cancel()
    }

}

