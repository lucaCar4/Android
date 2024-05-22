package com.example.foodandart

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.foodandart.service.BackGroundService
import com.example.foodandart.service.DateUpdateWorker
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

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
            "Food&Art",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.description = "Food&Art"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
        /*
        val serviceIntent = Intent(this, BackGroundService::class.java)
        this.startService(serviceIntent)

        val workRequest = PeriodicWorkRequest.Builder(
            DateUpdateWorker::class.java,
            15,
            TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueue(workRequest)

         */
    }
}