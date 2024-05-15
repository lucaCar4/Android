package com.example.foodandart.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.example.foodandart.MainActivity
import com.example.foodandart.R
import com.example.foodandart.reciver.MyReceiver
import kotlin.random.Random

class NotificationService(private val context : Context) {

    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun showWelcomeNotification() {
        val notification = NotificationCompat.Builder(context, "foodandart_channel")
            .setContentTitle("Welcome!")
            .setContentText("HI! Welcome to the application")
            .setSmallIcon(R.mipmap.foodandart_round)
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(Random.nextInt(), notification)
    }

    fun showWelcomeBackNotification() {
        val notification = NotificationCompat.Builder(context, "foodandart_channel")
            .setContentTitle("Welcome Back!")
            .setContentText("HI! Welcome back")
            .setSmallIcon(R.mipmap.foodandart_round)
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(Random.nextInt(), notification)
    }
    fun showCardUpdateNotification(cardId: String) {
        val intent = Intent(context, MyReceiver::class.java).apply {
            putExtra("MESSAGE", "Clicked!")
        }
        val flag = PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            flag
        )
        val clickIntent = Intent(
            Intent.ACTION_VIEW,
            "https://foodandart-d0115.web.app/card/${cardId}".toUri(),
            context,
            MainActivity::class.java
        )
        val clickPendingIntent : PendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(clickIntent)
            getPendingIntent(1, flag)!!

        }
        val notification = NotificationCompat.Builder(context, "foodandart_channel")
            .setContentTitle("New Date")
            .setContentText("There is a new date in one of your favorite card")
            .setSmallIcon(R.mipmap.foodandart_round)
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
            .setAutoCancel(true)
            .addAction(0, "ACTION", pendingIntent)
            .setContentIntent(clickPendingIntent)
            .build()
        notificationManager.notify(Random.nextInt(), notification)
    }

    private fun Context.bitmapFromResource(
        @DrawableRes resId: Int
    ) = BitmapFactory.decodeResource(resources, resId)
}