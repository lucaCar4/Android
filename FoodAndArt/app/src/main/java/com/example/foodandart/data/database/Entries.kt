package com.example.foodandart.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo
    val card: String,
    @ColumnInfo
    val date: String,
    @ColumnInfo
    val quantity: Int = 0

)