package com.eatssu.android.notification

import android.app.Activity
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Rect
import android.os.SystemClock
import android.view.MotionEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.app.NotificationManagerCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.eatssu.android.alarm.MenuNotificationFactory
import com.eatssu.android.alarm.NotificationPublisher
import com.eatssu.android.data.local.NotificationStore
import com.eatssu.android.domain.model.MenuFavoriteSearchResult
import com.eatssu.android.domain.model.notification.MenuNotification
import com.eatssu.android.domain.model.notification.NotificationSource
import com.eatssu.android.domain.usecase.alarm.FavoriteMealMatch
import com.eatssu.android.presentation.notification.NotificationActivity
import com.eatssu.common.enums.AppLanguage
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class NotificationDeliveryTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext
    private val clock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
    private val date = LocalDate.now(clock)
    private val factory = MenuNotificationFactory(context, clock)

    @Test
    fun exactCopiesAndDefaultDestination() {
        val items = listOf("파스타", "미역국", "김치").mapIndexed { index, name ->
            MenuFavoriteSearchResult(index.toLong() + 1, name, Restaurant.DORMITORY, false)
        }
        val one = factory.create(
            date,
            listOf(FavoriteMealMatch(date, Time.DINNER, items.take(1))),
            AppLanguage.KOREAN
        ).single()
        assertEquals("🍽️ 찜한 메뉴가 나온대요!", one.title)
        assertEquals("오늘 저녁, 기숙사식당에 파스타 등장 👀", one.body)
        val two = factory.create(
            date,
            listOf(FavoriteMealMatch(date, Time.LUNCH, items.take(2))),
            AppLanguage.KOREAN
        ).single()
        assertEquals("🍽️ 찜한 메뉴가 2개나 나온대요!", two.title)
        assertEquals("오늘 점심, 기숙사식당 파스타 · 기숙사식당 미역국 등장 👀", two.body)
        val many = factory.create(
            date,
            listOf(FavoriteMealMatch(date, Time.DINNER, items)),
            AppLanguage.KOREAN
        ).single()
        assertEquals("🍽️ 찜한 메뉴가 여러 개 나온대요!", many.title)
        assertEquals("오늘 저녁, 기숙사식당 파스타 외 2개 등장 👀", many.body)
        assertEquals(date.toString(), many.mealDate)
        assertEquals(Time.DINNER, many.mealTime)
        assertEquals(Restaurant.DORMITORY, many.targetRestaurant)
        val fallback = factory.create(date, emptyList(), AppLanguage.KOREAN).single()
        assertEquals(NotificationSource.LOCAL, fallback.source)
        assertEquals(null, fallback.mealDate)
        assertEquals(null, fallback.targetRestaurant)
    }

    @Test
    fun localAndRemoteShareInboxReadStateAndClickDestination() = runBlocking {
        val store = NotificationStore(context)
        val original = store.notifications.first()
        val publisher = NotificationPublisher(context, store)
        val suffix = UUID.randomUUID().toString()
        val local = factory.create(
            date, listOf(
                FavoriteMealMatch(
                    date, Time.DINNER, listOf(
                        MenuFavoriteSearchResult(1, "파스타", Restaurant.DORMITORY, false),
                    )
                )
            ), AppLanguage.KOREAN
        ).single().copy(id = "test-local-$suffix")
        val remote = requireNotNull(
            MenuNotification.fromRemoteData(
                mapOf("title" to "서버 공지", "body" to "콘솔에서 수신한 일반 메시지"),
                "test-remote-$suffix", clock.millis(), "알림",
            )
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        store.clear()
        try {
            publisher.publish(local)
            publisher.publish(remote)
            assertEquals(2, store.notifications.first().size)
            assertTrue(store.notifications.first().all { !it.isRead })
            if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                assertTrue(manager.activeNotifications.any { it.id == local.id.hashCode() })
                assertTrue(manager.activeNotifications.any { it.id == remote.id.hashCode() })
            }
            ActivityScenario.launchActivityForResult<NotificationActivity>(
                Intent(
                    context,
                    NotificationActivity::class.java
                )
            ).use { scenario ->
                tap(awaitText(local.title))
                val result = scenario.result
                assertEquals(Activity.RESULT_OK, result.resultCode)
                assertEquals(
                    date.toString(),
                    result.resultData.getStringExtra(NotificationActivity.EXTRA_MEAL_DATE)
                )
                assertEquals(
                    Time.DINNER.name,
                    result.resultData.getStringExtra(NotificationActivity.EXTRA_MEAL_TIME)
                )
                assertEquals(
                    Restaurant.DORMITORY.name,
                    result.resultData.getStringExtra(NotificationActivity.EXTRA_RESTAURANT)
                )
            }
            assertTrue(store.notifications.first().first { it.id == local.id }.isRead)
            assertFalse(store.notifications.first().first { it.id == remote.id }.isRead)
            store.markAllRead()
            publisher.publish(local)
            publisher.publish(remote)
            assertEquals(2, store.notifications.first().size)
            assertTrue(store.notifications.first().all { it.isRead })
            val expired = remote.copy(
                id = "expired-$suffix",
                receivedAt = clock.instant().minus(Duration.ofDays(8)).toEpochMilli()
            )
            assertFalse(store.addIfAbsent(expired))
        } finally {
            manager.cancel(local.id.hashCode())
            manager.cancel(remote.id.hashCode())
            store.clear()
            original.forEach { store.add(it) }
        }
    }

    private fun awaitText(text: String): AccessibilityNodeInfo {
        repeat(60) {
            findText(
                instrumentation.uiAutomation.rootInActiveWindow,
                text
            )?.let { Thread.sleep(300); return it }
            Thread.sleep(100)
        }
        error("Missing notification text: $text")
    }

    private fun findText(node: AccessibilityNodeInfo?, text: String): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.text?.toString() == text) return node
        for (index in 0 until node.childCount) findText(
            node.getChild(index),
            text
        )?.let { return it }
        return null
    }

    private fun tap(node: AccessibilityNodeInfo) {
        val bounds = Rect().also { node.getBoundsInScreen(it) }
        val downTime = SystemClock.uptimeMillis()
        listOf(MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP).forEach { action ->
            val event = MotionEvent.obtain(
                downTime,
                SystemClock.uptimeMillis(),
                action,
                bounds.centerX().toFloat(),
                bounds.centerY().toFloat(),
                0
            )
            instrumentation.sendPointerSync(event)
            event.recycle()
        }
    }
}
