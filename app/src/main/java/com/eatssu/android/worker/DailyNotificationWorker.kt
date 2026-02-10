package com.eatssu.android.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.eatssu.notification.R
import com.eatssu.notification.manager.EatSsuNotificationManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.DayOfWeek
import java.time.LocalDateTime

@HiltWorker
class DailyNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationManager: EatSsuNotificationManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val currentDay = LocalDateTime.now().dayOfWeek
        if (currentDay != DayOfWeek.SATURDAY && currentDay != DayOfWeek.SUNDAY) {
            val title = context.getString(R.string.notification_context_title)
            val body = context.getString(R.string.notification_context_text)
            
            notificationManager.showNotification(
                title = title,
                body = body,
                channelId = EatSsuNotificationManager.CHANNEL_LUNCH
            )
        }
        return Result.success()
    }
}
