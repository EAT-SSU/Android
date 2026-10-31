package com.eatssu.android.notification

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.SystemClock
import android.view.MotionEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.eatssu.android.data.local.NotificationStore
import com.eatssu.android.domain.model.notification.MenuNotification
import com.eatssu.android.presentation.notification.NotificationActivity
import com.eatssu.common.enums.Time
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class NotificationInboxTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext
    private val store = NotificationStore(context)

    @Test
    fun emptyUnreadAndReadStatesPersistAndRender() = runBlocking {
        store.clear()
        try {
            ActivityScenario.launch<NotificationActivity>(
                Intent(
                    context,
                    NotificationActivity::class.java
                )
            ).use {
                awaitText("새로운 알림이 없어요.")
                screenshot("empty")
                val now = System.currentTimeMillis()
                store.add(
                    MenuNotification(
                        "test-1",
                        "🍽️ 찜한 메뉴가 나온대요!",
                        "오늘 점심, 학생식당에 파스타 등장 👀",
                        now - 3_600_000,
                        "2026-08-10",
                        Time.LUNCH
                    )
                )
                store.add(
                    MenuNotification(
                        "test-2",
                        "🍽️ 찜한 메뉴가 2개나 나온대요!",
                        "오늘 점심, 도담식당 파스타 · 학생식당 파스타 등장 👀",
                        now - 86_400_000,
                        "2026-08-09",
                        Time.LUNCH,
                        true
                    )
                )
                store.add(
                    MenuNotification(
                        "test-3",
                        "🍽️ 찜한 메뉴가 여러 개 나온대요!",
                        "오늘 점심, 도담식당 파스타 외 2개 등장 👀",
                        now - 172_800_000,
                        "2026-08-08",
                        Time.LUNCH,
                        true
                    )
                )
                awaitText("모두 읽음")
                screenshot("unread")
                tap(
                    requireNotNull(
                        findText(
                            instrumentation.uiAutomation.rootInActiveWindow,
                            "모두 읽음"
                        )
                    )
                )
                repeat(50) {
                    if (store.notifications.first().all { it.isRead }) return@repeat
                    Thread.sleep(100)
                }
                assertTrue(store.notifications.first().all { it.isRead })
                screenshot("read")
                it.recreate()
                awaitText("모두 읽음")
                assertTrue(store.notifications.first().all { it.isRead })
            }
        } finally {
            store.clear()
        }
    }

    private fun awaitText(text: String) {
        repeat(50) {
            if (findText(instrumentation.uiAutomation.rootInActiveWindow, text) != null) {
                Thread.sleep(400)
                return
            }
            Thread.sleep(100)
        }
        error("Missing UI text: $text")
    }

    private fun findText(node: AccessibilityNodeInfo?, text: String): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.text?.toString() == text) return node
        for (index in 0 until node.childCount) {
            findText(node.getChild(index), text)?.let { return it }
        }
        return null
    }

    private fun tap(node: AccessibilityNodeInfo) {
        val bounds = Rect().also { node.getBoundsInScreen(it) }
        val now = SystemClock.uptimeMillis()
        listOf(MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP).forEach { action ->
            val event = MotionEvent.obtain(
                now,
                SystemClock.uptimeMillis(),
                action,
                bounds.centerX().toFloat(),
                bounds.centerY().toFloat(),
                0
            )
            instrumentation.sendPointerSync(event)
            event.recycle()
        }
        Thread.sleep(500)
    }

    private fun screenshot(name: String) {
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        File(context.filesDir, "notification-$name.png").outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        bitmap.recycle()
    }
}
