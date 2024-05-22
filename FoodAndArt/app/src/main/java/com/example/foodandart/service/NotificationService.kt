package com.example.foodandart.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.foodandart.MainActivity
import com.example.foodandart.R
import com.example.foodandart.reciver.MyReceiver
import com.example.foodandart.ui.FoodAndArtRoute
import kotlin.random.Random

class NotificationService(private val context : Context) {

    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun showWelcomeNotification() {
        val notification = NotificationCompat.Builder(context, "foodandart_channel")
            .setContentTitle(context.getString(R.string.welcome))
            .setSmallIcon(R.mipmap.foodandart_round)
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(Random.nextInt(), notification)
    }

    fun showWelcomeBackNotification() {
        val notification = NotificationCompat.Builder(context, "foodandart_channel")
            .setContentTitle(context.getString(R.string.welcome_back))
            .setSmallIcon(R.mipmap.foodandart_round)
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(Random.nextInt(), notification)
    }

    fun showCardUpdateNotification(cardId: String, name:String) {
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
            .setContentTitle(context.getString(R.string.new_date))
            .setContentText("${context.getString(R.string.new_date_text)} $name")
            .setSmallIcon(R.mipmap.foodandart_round)
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
            .setAutoCancel(true)
            .addAction(0, "", pendingIntent)
            .setContentIntent(clickPendingIntent)
            .build()
        notificationManager.notify(Random.nextInt(), notification)
    }

    private fun Context.bitmapFromResource(
        @DrawableRes resId: Int
    ) = BitmapFactory.decodeResource(resources, resId)
}
