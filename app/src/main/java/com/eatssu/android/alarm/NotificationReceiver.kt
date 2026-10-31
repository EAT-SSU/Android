package com.eatssu.android.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.domain.usecase.alarm.AlarmUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {
    @Inject
    lateinit var settings: SettingDataStore
    @Inject
    lateinit var alarmUseCase: AlarmUseCase
    @Inject
    lateinit var clock: Clock

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action in listOf(
                Intent.ACTION_BOOT_COMPLETED,
                Intent.ACTION_MY_PACKAGE_REPLACED,
                Intent.ACTION_TIME_CHANGED,
                Intent.ACTION_TIMEZONE_CHANGED
            )
        ) {
            val pending = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    if (settings.dailyNotificationStatus.first()) alarmUseCase.scheduleAlarm()
                } catch (error: Exception) {
                    Timber.w(error, "일일 알림 재예약 실패")
                } finally {
                    pending.finish()
                }
            }
        } else {
            val date = LocalDate.now(clock.withZone(ZoneId.systemDefault()))
            if (date.dayOfWeek !in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)) {
                DailyMenuNotificationWorker.enqueue(context, date)
            }
        }
    }
}
