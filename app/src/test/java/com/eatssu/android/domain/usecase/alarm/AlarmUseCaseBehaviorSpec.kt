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
import java.util.Calendar

class AlarmUseCaseBehaviorSpec : AppBehaviorSpec({

    given("AlarmUseCase") {
        val context = mockk<Context>()
        val alarmManager = mockk<AlarmManager>(relaxed = true)
        val pendingIntent = mockk<PendingIntent>()
        val calendar = mockk<Calendar>(relaxed = true)
        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        every { context.getSystemService(Context.ALARM_SERVICE) } returns alarmManager
        mockkStatic(PendingIntent::class)
        every { PendingIntent.getBroadcast(context, 0, any(), flags) } returns pendingIntent
        mockkStatic(Calendar::class)
        every { Calendar.getInstance() } returns calendar
        every { calendar.set(Calendar.HOUR_OF_DAY, 11) } returns Unit
        every { calendar.set(Calendar.MINUTE, 0) } returns Unit
        every { calendar.set(Calendar.SECOND, 0) } returns Unit
        every { calendar.set(Calendar.MILLISECOND, 0) } returns Unit
        every { calendar.add(any(), any()) } returns Unit

        val useCase = AlarmUseCase(context)

        `when`("알람 시각이 현재 시각보다 과거면") {
            every { calendar.timeInMillis } returnsMany listOf(0L, 86_400_000L)

            then("다음 날로 하루 추가 후 repeating 알람을 등록한다") {
                useCase.scheduleAlarm()

                verify(exactly = 1) { calendar.add(Calendar.DAY_OF_YEAR, 1) }
                verify(exactly = 1) {
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        86_400_000L,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent,
                    )
                }
            }
        }

        `when`("알람 시각이 현재 시각보다 미래면") {
            every { calendar.timeInMillis } returnsMany listOf(Long.MAX_VALUE, Long.MAX_VALUE)

            then("하루 추가 없이 repeating 알람을 등록한다") {
                useCase.scheduleAlarm()

                verify(exactly = 0) { calendar.add(Calendar.DAY_OF_YEAR, 1) }
                verify(exactly = 1) {
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        Long.MAX_VALUE,
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
