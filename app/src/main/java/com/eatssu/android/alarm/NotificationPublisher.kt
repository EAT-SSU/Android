package com.eatssu.android.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.eatssu.android.R
import com.eatssu.android.data.local.NotificationStore
import com.eatssu.android.domain.model.notification.MenuNotification
import com.eatssu.android.domain.model.notification.NotificationSource
import com.eatssu.android.presentation.intro.IntroActivity
import com.eatssu.android.presentation.notification.NotificationActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationPublisher @Inject constructor(
    @ApplicationContext private val context: Context,
    private val store: NotificationStore,
) {
    suspend fun publish(message: MenuNotification) {
        if (!store.addIfAbsent(message)) return
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return
        val local = message.source == NotificationSource.LOCAL
        val channelId = if (local) "DailyNotificationChannel" else "FCMNotificationChannel"
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                channelId,
                context.getString(if (local) R.string.notification_channel_lunch_name else R.string.notification_channel_server_name),
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description =
                    context.getString(if (local) R.string.notification_channel_lunch_description else R.string.notification_channel_server_description)
                enableLights(true)
                enableVibration(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            })
        val intent = Intent(context, IntroActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("launch_path", if (local) "local_notification" else "remote_notification")
            putExtra("notification_id", message.id)
            putExtra(NotificationActivity.EXTRA_OPEN_CAFETERIA, true)
            putExtra(NotificationActivity.EXTRA_MEAL_DATE, message.mealDate)
            putExtra(NotificationActivity.EXTRA_MEAL_TIME, message.mealTime?.name)
            putExtra(NotificationActivity.EXTRA_RESTAURANT, message.targetRestaurant?.name)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, message.id.hashCode(), intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_mini_logo)
            .setContentTitle(message.title)
            .setContentText(message.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message.body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        try {
            manager.notify(message.id.hashCode(), notification)
        } catch (error: SecurityException) {
            Timber.w(error, "알림 표시 권한이 변경됨")
        }
    }
}
