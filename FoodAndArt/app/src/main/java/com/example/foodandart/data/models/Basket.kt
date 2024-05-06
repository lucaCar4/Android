package com.example.foodandart.data.models

import com.example.foodandart.data.database.BasketElem
import kotlinx.coroutines.Job

data class BasketState(val basket: List<BasketElem>)
interface BasketActions {
    fun addElem(elem: BasketElem): Job
    fun removeElem(elem: BasketElem): Job

    fun updateElem(elem: BasketElem): Job

}