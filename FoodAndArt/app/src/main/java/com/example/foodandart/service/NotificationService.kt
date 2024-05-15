package com.example.foodandart.service

import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import com.example.foodandart.R
import kotlin.random.Random

class NotificationService(private val context : Context) {

    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun showWelcomeNotification() {
        val notification = NotificationCompat.Builder(context, "foodandart_channel")
            .setContentTitle("Welcome!")
            .setContentText("HI! Welcome to the application")
            .setSmallIcon(R.drawable.foodandart)
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(Random.nextInt(), notification)
    }

    fun showWelcomeBackNotification() {
        val notification = NotificationCompat.Builder(context, "foodandart_channel")
            .setContentTitle("Welcome Back!")
            .setContentText("HI! Welcome back")
            .setSmallIcon(R.drawable.foodandart)
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(Random.nextInt(), notification)
    }

    private fun Context.bitmapFromResource(
        @DrawableRes resId: Int
    ) = BitmapFactory.decodeResource(resources, resId)
}