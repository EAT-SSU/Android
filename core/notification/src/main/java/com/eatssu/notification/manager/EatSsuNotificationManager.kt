package com.eatssu.notification.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.eatssu.notification.R
import com.eatssu.notification.navigation.NotificationNavigationProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class EatSsuNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val navigationProvider: NotificationNavigationProvider
) {

    fun showNotification(
        title: String?,
        body: String?,
        channelId: String = CHANNEL_SERVER
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager, channelId)

        val intent = navigationProvider.getLaunchIntent().apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Add extra to identify source if needed
            putExtra("launch_path", if (channelId == CHANNEL_LUNCH) "local_notification" else "remote_notification")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_mini_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(if (channelId == CHANNEL_LUNCH) NotificationCompat.CATEGORY_ALARM else NotificationCompat.CATEGORY_EVENT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        // Use a unique ID based on channel/time or fixed
        val notificationId = if (channelId == CHANNEL_LUNCH) 1 else System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val (nameRes, descRes) = when (channelId) {
                CHANNEL_LUNCH -> R.string.notification_channel_lunch_name to R.string.notification_channel_lunch_description
                else -> R.string.notification_channel_server_name to R.string.notification_channel_server_description
            }
            
            val name = context.getString(nameRes)
            val descriptionText = context.getString(descRes)
            val importance = NotificationManager.IMPORTANCE_HIGH
            
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_LUNCH = "DailyNotificationChannel"
        const val CHANNEL_SERVER = "FCMNotificationChannel"
    }
}
