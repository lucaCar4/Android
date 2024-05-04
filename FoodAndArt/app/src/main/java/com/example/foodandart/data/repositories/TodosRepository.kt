package com.example.foodandart.data.repositories

import com.example.foodandart.data.database.Todo
import com.example.foodandart.data.database.TodosDAO
import kotlinx.coroutines.flow.Flow

class TodosRepository(
    private val todosDAO: TodosDAO
) {
    val todos = todosDAO.getAll()

    suspend fun upsert(todo: Todo) = todosDAO.upsert(todo)

    suspend fun delete(todo: Todo) = todosDAO.delete(todo)
}