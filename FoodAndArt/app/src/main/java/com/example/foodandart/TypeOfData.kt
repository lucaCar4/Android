package com.example.foodandart

import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Color
import com.example.foodandart.service.NotificationService

val languages = listOf("it", "en")
val cardTypes = mapOf("Restaurant" to Color(0xFFFF9800), "Museum" to Color(0xFFFFEB3B), "Package" to Color(0xFFFFC107))

@SuppressLint("StaticFieldLeak")
var notificationService: NotificationService? = null
