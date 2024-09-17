package com.eatssu.android.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.eatssu.android.R
import com.eatssu.android.ui.main.MainActivity

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        showNotification(context)
    }

    private fun showNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "점심시간 전 알림",
                NotificationManager.IMPORTANCE_HIGH // 중요도를 높게 설정
            ).apply {
                description = "점심시간 전, 푸시알림을 발송합니다."
                enableLights(true)
                enableVibration(true)  // 진동도 활성화
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC // 잠금 화면에서도 표시
            }
            notificationManager.createNotificationChannel(channel)
        }


        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.img_new_logo)
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
