package com.example.foodandart

import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Color
import com.example.foodandart.service.NotificationService

val languages = listOf("it", "en")
val cardTypes = mapOf("Restaurant" to Color.Red, "Museum" to Color.Green, "Package" to Color.Yellow)

@SuppressLint("StaticFieldLeak")
var notificationService: NotificationService? = null
