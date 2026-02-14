package com.eatssu.android.domain.usecase.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify

class AlarmUseCaseBehaviorSpec : AppBehaviorSpec({

    given("AlarmUseCase") {
        val context = mockk<Context>()
        val alarmManager = mockk<AlarmManager>(relaxed = true)
        val pendingIntent = mockk<PendingIntent>()
        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        every { context.getSystemService(Context.ALARM_SERVICE) } returns alarmManager
        mockkStatic(PendingIntent::class)
        every { PendingIntent.getBroadcast(context, 0, any(), flags) } returns pendingIntent

        val useCase = AlarmUseCase(context)

        `when`("scheduleAlarm을 호출하면") {
            then("repeating 알람을 등록한다") {
                useCase.scheduleAlarm()

                verify(exactly = 1) {
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        any(),
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent,
                    )
                }
            }
        }

        `when`("cancelAlarm을 호출하면") {
            then("등록했던 pendingIntent로 알람을 취소한다") {
                useCase.cancelAlarm()
                verify(exactly = 1) { alarmManager.cancel(pendingIntent) }
            }
        }

        then("PendingIntent 생성 플래그는 고정 값을 사용한다") {
            flags shouldBe (PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
})
