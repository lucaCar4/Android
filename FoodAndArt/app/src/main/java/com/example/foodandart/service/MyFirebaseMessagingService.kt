package com.example.foodandart.service

import android.app.PendingIntent
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.foodandart.MainActivity
import com.example.foodandart.R
import com.google.firebase.messaging.FirebaseMessagingService

const val channelId = "notification_channel"
const val channelName = "com.example.foodandart"
class MyFirebaseMessagingService : FirebaseMessagingService() {

    fun generateNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        var builder : NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId )
            //.setSmallIcon()
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
        //2builder = builder.setContent(getRemoteView(title, message))



    }

}