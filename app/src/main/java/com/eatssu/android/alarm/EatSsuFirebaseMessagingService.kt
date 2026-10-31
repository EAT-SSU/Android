package com.eatssu.android.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.eatssu.android.R
import com.eatssu.android.data.local.NotificationStore
import com.eatssu.android.domain.model.notification.MenuNotification
import com.eatssu.android.presentation.intro.IntroActivity
import com.eatssu.android.presentation.notification.NotificationActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class EatSsuFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var notificationStore: NotificationStore

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val body = remoteMessage.data["body"] ?: remoteMessage.notification?.body ?: return
        val title = remoteMessage.data["title"] ?: remoteMessage.notification?.title
        val sentAt = remoteMessage.sentTime.takeIf { it > 0 } ?: System.currentTimeMillis()
        val data = remoteMessage.data.toMutableMap().apply {
            put("body", body)
            title?.let { put("title", it) }
        }
        val notification = MenuNotification.fromData(
            data,
            remoteMessage.messageId ?: "${sentAt}_${data.hashCode()}",
            sentAt,
        )
        // Complete the small local write inside the FCM callback lifetime.
        if (notification != null) runBlocking(Dispatchers.IO) { notificationStore.add(notification) }
        sendNotification(body, title, notification)
    }

    override fun onNewToken(token: String) {
        // Token registration is not currently used. Never log the token.
    }

    private fun sendNotification(
        messageBody: String,
        title: String?,
        menuNotification: MenuNotification?
    ) {
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

        val intent = Intent(this, IntroActivity::class.java).apply {
            putExtra("launch_path", "remote_notification")
            menuNotification?.let {
                putExtra(NotificationActivity.EXTRA_MEAL_DATE, it.mealDate)
                putExtra(NotificationActivity.EXTRA_MEAL_TIME, it.mealTime.name)
                putExtra("notification_id", it.id)
            }
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            menuNotification?.id?.hashCode() ?: 0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_mini_logo)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

        notificationManager.notify(
            menuNotification?.id?.hashCode() ?: NOTIFICATION_ID,
            notification
        )
    }

    companion object {
        private const val CHANNEL_ID = "FCMNotificationChannel"
        private const val NOTIFICATION_ID = 2
    }
}