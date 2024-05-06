package com.example.foodandart.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
@Dao
interface BasketDAO {
    @Query("SELECT * FROM basketelem ")
    fun getAll():Flow<List<BasketElem>>

    @Upsert
    suspend fun upsert(elem : BasketElem)

    @Delete
    suspend fun delete(elem : BasketElem)

    @Update
    suspend fun update(elem: BasketElem)
}