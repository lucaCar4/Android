package com.example.foodandart.data.repositories

import com.example.foodandart.data.database.BasketDAO
import com.example.foodandart.data.database.BasketElem
import kotlinx.coroutines.flow.Flow

class BasketRepository(private val basketDAO: BasketDAO) {
    val basket: Flow<List<BasketElem>> = basketDAO.getAll()

    suspend fun upsert(elem: BasketElem) = basketDAO.upsert(elem)
    suspend fun delete(elem: BasketElem) = basketDAO.delete(elem)
    suspend fun update(elem: BasketElem) = basketDAO.update(elem)
}