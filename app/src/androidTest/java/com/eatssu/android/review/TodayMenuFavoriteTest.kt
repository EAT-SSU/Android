package com.eatssu.android.review

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.SystemClock
import android.view.MotionEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.eatssu.android.R
import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.presentation.MainActivity
import com.eatssu.android.presentation.cafeteria.review.ReviewComposeActivity
import com.eatssu.android.presentation.favorite.FavoriteFragment
import com.eatssu.common.enums.MenuType
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicReference

@RunWith(AndroidJUnit4::class)
class TodayMenuFavoriteTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext

    @Test
    fun onlyTodayShowsButtonAndDisabledNotificationsKeepUserOnReview() = runBlocking {
        val settings = SettingDataStore(context)
        val originalSetting = settings.dailyNotificationStatus.first()
        settings.setDailyNotificationStatus(false)
        try {
            val today = LocalDate.now()
            listOf(today.minusDays(1), today.plusDays(1), today).forEach { date ->
                val intent = Intent(context, ReviewComposeActivity::class.java).apply {
                    putExtra("menuType", MenuType.VARIABLE.name)
                    putExtra("restaurant", Restaurant.HAKSIK.name)
                    putExtra("itemId", 1L)
                    putExtra("itemName", "오늘의 메뉴")
                    putExtra(ReviewComposeActivity.EXTRA_MENU_DATE, date.toString())
                }
                ActivityScenario.launch<ReviewComposeActivity>(intent).use { scenario ->
                    awaitText(context.getString(R.string.today_menu))
                    val label = context.getString(R.string.review_today_menu_favorite)
                    if (date != today) {
                        assertNull(findText(instrumentation.uiAutomation.rootInActiveWindow, label))
                    } else {
                        val button = awaitText(label)
                        screenshot("review-today-menu-favorite")
                        tap(button)
                        awaitText(context.getString(R.string.dialog_notification_permission_title))
                        assertEquals(Lifecycle.State.RESUMED, scenario.state)
                    }
                }
            }
        } finally {
            settings.setDailyNotificationStatus(originalSetting)
        }
    }

    @Test
    fun enabledNotificationsOpenMenuFavorites() = runBlocking {
        assumeTrue(NotificationManagerCompat.from(context).areNotificationsEnabled())
        val settings = SettingDataStore(context)
        val originalSetting = settings.dailyNotificationStatus.first()
        val launchedIntent = AtomicReference<Intent>()
        val monitor = object : Instrumentation.ActivityMonitor() {
            override fun onStartActivity(intent: Intent): Instrumentation.ActivityResult? {
                if (intent.component?.className != MainActivity::class.java.name) return null
                launchedIntent.set(intent)
                // Verify the destination without running unrelated login/network startup.
                return Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null)
            }
        }
        instrumentation.addMonitor(monitor)
        settings.setDailyNotificationStatus(true)
        try {
            val intent = Intent(context, ReviewComposeActivity::class.java).apply {
                putExtra("menuType", MenuType.VARIABLE.name)
                putExtra("restaurant", Restaurant.HAKSIK.name)
                putExtra("itemId", 1L)
                putExtra("itemName", "오늘의 메뉴")
                putExtra(ReviewComposeActivity.EXTRA_MENU_DATE, LocalDate.now().toString())
                putExtra(ReviewComposeActivity.EXTRA_MEAL_TIME, Time.MORNING.name)
            }
            ActivityScenario.launch<ReviewComposeActivity>(intent).use {
                tap(awaitText(context.getString(R.string.review_today_menu_favorite)))
                repeat(50) {
                    if (launchedIntent.get() == null) Thread.sleep(100)
                }
                val destination = requireNotNull(launchedIntent.get())
                assertTrue(
                    destination.getBooleanExtra(
                        MainActivity.EXTRA_OPEN_MENU_FAVORITES,
                        false
                    )
                )
                assertTrue(destination.flags and Intent.FLAG_ACTIVITY_CLEAR_TOP != 0)
                assertEquals(
                    Restaurant.HAKSIK.name,
                    destination.getStringExtra(FavoriteFragment.ARG_TODAY_RESTAURANT)
                )
                assertEquals(
                    Time.MORNING.name,
                    destination.getStringExtra(FavoriteFragment.ARG_TODAY_TIME)
                )
                assertTrue(destination.getBooleanExtra(FavoriteFragment.ARG_TODAY_EXPANDED, false))
            }
        } finally {
            instrumentation.removeMonitor(monitor)
            settings.setDailyNotificationStatus(originalSetting)
        }
    }

    private fun awaitText(text: String): AccessibilityNodeInfo {
        repeat(60) {
            findText(instrumentation.uiAutomation.rootInActiveWindow, text)?.let {
                Thread.sleep(400)
                return it
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

    private fun screenshot(name: String) {
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        File(context.filesDir, "$name.png").outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        bitmap.recycle()
    }
}
