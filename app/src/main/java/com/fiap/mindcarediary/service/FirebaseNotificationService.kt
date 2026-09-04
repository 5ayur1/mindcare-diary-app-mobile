package com.fiap.mindcarediary.service

import android.Manifest
import com.fiap.mindcarediary.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseNotificationService :
    FirebaseMessagingService() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(
        message: RemoteMessage
    ) {

        val title =
            message.notification?.title
                ?: "MindCare Diary"

        val body =
            message.notification?.body
                ?: "Você possui uma nova notificação."

        createNotificationChannel()

        val notification =
            NotificationCompat.Builder(
                this,
                CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(body)
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat
            .from(this)
            .notify(
                System.currentTimeMillis().toInt(),
                notification
            )
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "MindCare Diary",
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.description =
                "Notificações do MindCare Diary"

            val manager =
                getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID =
            "mindcare_notifications"
    }
}