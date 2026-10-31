package com.eatssu.android.presentation.favorite

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.SystemClock
import android.view.MotionEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.eatssu.android.R
import com.eatssu.android.domain.model.FavoriteMenu
import com.eatssu.android.domain.model.MenuFavoriteSearchResult
import com.eatssu.common.UiState
import com.eatssu.common.enums.Restaurant
import com.eatssu.design_system.theme.EatssuTheme
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class TodayMenuAccordionUiTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext

    @Test
    fun accordionSelectionAndUnavailableTimes() {
        val menus = listOf("맑은미역국", "쌀밥&흑미밥", "연탄불맛돈불고기", "양배추찜&쌈다시마/초장", "배추김치", "요구르트")
            .mapIndexed { index, name ->
                MenuFavoriteSearchResult(
                    index.toLong() + 1,
                    name,
                    Restaurant.HAKSIK,
                    false
                )
            }
        val todayState = mutableStateOf(
            TodayMenuFavoriteState(
                menus = menus,
                loadState = MenuFavoriteLoadState.Success
            )
        )
        ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.enableEdgeToEdge()
                activity.setContent {
                    EatssuTheme {
                        FavoriteScreen(
                            uiState = UiState.Success(
                                FavoriteState(
                                    favoriteMenus = listOf(
                                        FavoriteMenu(
                                            20,
                                            "파스타",
                                            Restaurant.DODAM,
                                            false
                                        )
                                    ),
                                    favoriteMenuLoadState = MenuFavoriteLoadState.Success,
                                )
                            ),
                            onBackToMap = {}, onStoreTypeSelected = {}, onPartnershipClick = {},
                            todayMenuState = todayState.value,
                            onTodayToggleExpanded = {
                                todayState.value =
                                    todayState.value.copy(expanded = !todayState.value.expanded)
                            },
                            onTodayToggleMenu = { id ->
                                val state = todayState.value
                                todayState.value =
                                    state.copy(selectedMenuIds = if (id in state.selectedMenuIds) state.selectedMenuIds - id else state.selectedMenuIds + id)
                            },
                            onTodayChangeMeal = { restaurant, time ->
                                todayState.value = todayState.value.copy(
                                    restaurant = restaurant,
                                    time = time,
                                    selectedMenuIds = emptySet()
                                )
                            },
                        )
                    }
                }
            }
            awaitText(R.string.today_favorite_title)
            assertNull(
                findText(
                    instrumentation.uiAutomation.rootInActiveWindow,
                    context.getString(R.string.today_favorite_change)
                )
            )
            screenshot("today-favorite-collapsed")
            tap(awaitText(R.string.today_favorite_title))
            awaitText(R.string.today_favorite_change)
            assertFalse(actionNode(awaitText(R.string.today_favorite_save)).isEnabled)
            screenshot("today-favorite-expanded")
            tap(awaitText("연탄불맛돈불고기"))
            assertTrue(
                actionNode(
                    awaitText(
                        context.getString(
                            R.string.today_favorite_save_count,
                            1
                        )
                    )
                ).isEnabled
            )
            screenshot("today-favorite-selected")
            tap(awaitText(R.string.today_favorite_change))
            awaitText(R.string.today_favorite_sheet_title)
            assertFalse(actionNode(awaitText(R.string.widget_dinner)).isEnabled)
            tap(awaitText(R.string.widget_morning))
            tap(awaitText(R.string.today_favorite_dormitory))
            assertFalse(actionNode(awaitText(R.string.widget_morning)).isEnabled)
            assertFalse(actionNode(awaitText(R.string.today_favorite_apply)).isEnabled)
            tap(awaitText(R.string.widget_dinner))
            assertTrue(actionNode(awaitText(R.string.today_favorite_apply)).isEnabled)
            screenshot("today-favorite-sheet")
            tap(awaitText(R.string.today_favorite_faculty))
            assertFalse(actionNode(awaitText(R.string.widget_morning)).isEnabled)
            assertFalse(actionNode(awaitText(R.string.widget_dinner)).isEnabled)
            assertFalse(actionNode(awaitText(R.string.today_favorite_apply)).isEnabled)
            tap(awaitText(R.string.widget_lunch))
            assertTrue(actionNode(awaitText(R.string.today_favorite_apply)).isEnabled)
            tap(awaitText(R.string.today_favorite_apply))
            awaitText(R.string.today_favorite_title)
            awaitText(R.string.today_favorite_faculty)
            assertFalse(actionNode(awaitText(R.string.today_favorite_save)).isEnabled)
        }
    }

    private fun awaitText(id: Int) = awaitText(context.getString(id))
    private fun awaitText(text: String): AccessibilityNodeInfo {
        repeat(60) {
            findText(instrumentation.uiAutomation.rootInActiveWindow, text)?.let {
                Thread.sleep(250)
                return it
            }
            Thread.sleep(100)
        }
        error("Missing UI text: $text")
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

    private fun actionNode(node: AccessibilityNodeInfo): AccessibilityNodeInfo {
        if (node.isClickable || node.isCheckable || node.className?.toString() == "android.widget.Button") return node
        return node.parent?.let(::actionNode) ?: node
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
        Thread.sleep(300)
    }

    private fun screenshot(name: String) {
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        File(context.filesDir, "$name.png").outputStream()
            .use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
    }
}
