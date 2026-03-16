package com.eatssu.android.domain.usecase.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import java.time.Clock
import java.time.ZoneId
import java.time.ZonedDateTime

class AlarmUseCaseBehaviorSpec : AppBehaviorSpec({

    given("AlarmUseCase") {
        val context = mockk<Context>()
        val alarmManager = mockk<AlarmManager>(relaxed = true)
        val pendingIntent = mockk<PendingIntent>()
        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        every { context.getSystemService(Context.ALARM_SERVICE) } returns alarmManager
        mockkStatic(PendingIntent::class)
        every { PendingIntent.getBroadcast(context, 0, any(), flags) } returns pendingIntent

        `when`("현재 시간이 11시 이전이면") {
            val zone = ZoneId.systemDefault()
            val now = ZonedDateTime.of(2025, 1, 1, 10, 30, 0, 0, zone)
            val clock = Clock.fixed(now.toInstant(), zone)
            val useCase = AlarmUseCase(context, clock)
            val triggerAtSlot = slot<Long>()

            then("당일 11시로 repeating 알람을 등록한다") {
                useCase.scheduleAlarm()

                verify(exactly = 1) {
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        capture(triggerAtSlot),
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent,
                    )
                }

                val expected = now
                    .withHour(11)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0)
                    .toInstant()
                    .toEpochMilli()

                triggerAtSlot.captured shouldBe expected
            }
        }

        `when`("현재 시간이 11시 이상이면") {
            val zone = ZoneId.systemDefault()
            val now = ZonedDateTime.of(2025, 1, 1, 11, 0, 0, 0, zone)
            val clock = Clock.fixed(now.toInstant(), zone)
            val useCase = AlarmUseCase(context, clock)
            val triggerAtSlot = slot<Long>()

            then("다음날 11시로 repeating 알람을 등록한다") {
                useCase.scheduleAlarm()

                verify(exactly = 1) {
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        capture(triggerAtSlot),
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent,
                    )
                }

                val expected = now
                    .plusDays(1)
                    .withHour(11)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0)
                    .toInstant()
                    .toEpochMilli()

                triggerAtSlot.captured shouldBe expected
            }
        }

        `when`("cancelAlarm을 호출하면") {
            val zone = ZoneId.systemDefault()
            val clock = Clock.fixed(
                ZonedDateTime.of(2025, 1, 1, 10, 0, 0, 0, zone).toInstant(),
                zone,
            )
            val useCase = AlarmUseCase(context, clock)

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
