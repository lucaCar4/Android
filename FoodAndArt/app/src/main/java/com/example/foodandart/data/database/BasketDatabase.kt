package com.example.foodandart.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BasketElem::class], version = 1)
abstract class BasketDatabase : RoomDatabase() {
    abstract fun basketDAO(): BasketDAO
}