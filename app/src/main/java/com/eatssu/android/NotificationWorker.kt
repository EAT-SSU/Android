package com.eatssu.android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.eatssu.android.ui.main.MainActivity

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Daily Notification Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 메인 액티비티로 이동하는 Intent 생성
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // PendingIntent 생성
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.img_new_logo)
            .setContentTitle(applicationContext.getString(R.string.notification_context_title))
            .setContentText(applicationContext.getString(R.string.notification_context_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 높은 우선순위 설정
            .setCategory(NotificationCompat.CATEGORY_ALARM) // 알람 카테고리로 설정
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // 잠금 화면에서도 표시
            .setAutoCancel(true)
            .setContentIntent(pendingIntent) // 클릭 시 실행할 PendingIntent 설정

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    companion object {
        private const val CHANNEL_ID = "DailyNotificationChannel"
        private const val NOTIFICATION_ID = 1
    }
}