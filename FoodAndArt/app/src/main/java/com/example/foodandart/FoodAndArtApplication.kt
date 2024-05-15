package com.example.foodandart

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import com.example.foodandart.service.BackGroundService
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class FoodAndArtApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@FoodAndArtApplication)
            modules(appModule)
        }
        val notificationChannel = NotificationChannel(
            "foodandart_channel",
            "Food And Art",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.description = "Ciaooo"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
        val serviceIntent = Intent(this, BackGroundService::class.java)
        this.startService(serviceIntent)
    }
}