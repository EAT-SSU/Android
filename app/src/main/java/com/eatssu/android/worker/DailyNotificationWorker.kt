package com.eatssu.android.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.eatssu.android.R
import com.eatssu.android.presentation.intro.IntroActivity
import java.time.DayOfWeek
import java.time.LocalDateTime

class DailyNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val currentDay = LocalDateTime.now().dayOfWeek
        if (currentDay != DayOfWeek.SATURDAY && currentDay != DayOfWeek.SUNDAY) {
            showNotification(context)
        }
        return Result.success()
    }

    private fun showNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_lunch_name),
            NotificationManager.IMPORTANCE_HIGH // 중요도를 높게 설정
        ).apply {
            description = context.getString(R.string.notification_channel_lunch_description)
            enableLights(true)
            enableVibration(true)  // 진동도 활성화
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC // 잠금 화면에서도 표시
        }
        notificationManager.createNotificationChannel(channel)
        val intent = Intent(context, IntroActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent.putExtra("launch_path", "local_notification"),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mini_logo)
            .setContentTitle(context.getString(R.string.notification_context_title))
            .setContentText(context.getString(R.string.notification_context_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "DailyNotificationChannel"
        private const val NOTIFICATION_ID = 1
    }
}
