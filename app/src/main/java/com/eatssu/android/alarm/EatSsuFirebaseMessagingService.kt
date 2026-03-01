package com.eatssu.android.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.eatssu.android.R
import com.eatssu.android.presentation.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber


class EatSsuFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("From: ${remoteMessage.from}")

        val notificationBody = remoteMessage.notification?.body ?: return
        Timber.d("Message Notification Body: $notificationBody")

        sendNotification(notificationBody)
    }


    override fun onNewToken(token: String) {
        Timber.d("Refreshed token: $token")
        // 아직 특정 기기의 Push Token이 필요하지 않음
    }

    private fun sendNotification(messageBody: String?) {
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_server_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = getString(R.string.notification_channel_server_description)
            enableLights(true)
            enableVibration(true)  // 진동도 활성화
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC // 잠금 화면에서도 표시
        }
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("launch_path", "remote_notification")
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_mini_logo)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "FCMNotificationChannel"
        private const val NOTIFICATION_ID = 2
    }
}
