package com.example.foodandart.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.foodandart.data.database.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodosDAO {

    @Query("SELECT * FROM todo")
    fun getAll() : Flow<List<Todo>>

    @Upsert
    suspend fun upsert(todo : Todo)

    @Delete
    suspend fun delete(todo: Todo)
}
