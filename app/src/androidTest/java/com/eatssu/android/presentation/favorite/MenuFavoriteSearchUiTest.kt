package com.eatssu.android.presentation.favorite

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class MenuFavoriteSearchUiTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext

    @Test
    fun focusTypingEnterFeedbackAndClose() {
        val requests = AtomicInteger()
        val state =
            mutableStateOf(FavoriteState(favoriteMenuLoadState = MenuFavoriteLoadState.Success))
        val today = mutableStateOf(
            TodayMenuFavoriteState(
                expanded = true,
                loadState = MenuFavoriteLoadState.Success
            )
        )
        val feedback = mutableStateOf<FavoriteMenuSnackbarEvent?>(null)
        launch {
            FavoriteScreen(
                uiState = UiState.Success(state.value),
                onBackToMap = {}, onStoreTypeSelected = {}, onPartnershipClick = {},
                todayMenuState = today.value,
                menuSnackbarEvent = feedback.value,
                onMenuSearchFocusChanged = { focused ->
                    if (focused) state.value = state.value.copy(isMenuSearchMode = true)
                },
                onMenuSearchQueryChanged = { query ->
                    state.value = state.value.copy(
                        menuSearchQuery = query,
                        submittedMenuSearchQuery = null,
                        menuSearchResults = emptyList(),
                        menuSearchLoadState = MenuFavoriteLoadState.Idle
                    )
                },
                onMenuSearchSubmit = {
                    val keyword = state.value.normalizedMenuSearchQuery
                    if (keyword.length >= 2) {
                        requests.incrementAndGet()
                        val failed = keyword == "zz"
                        state.value = state.value.copy(
                            submittedMenuSearchQuery = keyword,
                            menuSearchLoadState = if (failed) MenuFavoriteLoadState.Error else MenuFavoriteLoadState.Success,
                            menuSearchResults = if (failed) emptyList() else listOf(
                                MenuFavoriteSearchResult(1, "Meatballs", Restaurant.HAKSIK, false)
                            ),
                        )
                        if (failed) feedback.value =
                            FavoriteMenuSnackbarEvent(R.string.favorite_menu_load_error, true)
                    }
                },
                onMenuSearchExit = {
                    state.value = state.value.copy(
                        isMenuSearchMode = false,
                        menuSearchQuery = "",
                        submittedMenuSearchQuery = null,
                        menuSearchResults = emptyList(),
                        menuSearchLoadState = MenuFavoriteLoadState.Idle
                    )
                    today.value = today.value.copy(expanded = false)
                },
                onSearchFavoriteClick = { menu ->
                    state.value =
                        state.value.copy(menuSearchResults = listOf(menu.copy(isFavorite = !menu.isFavorite)))
                    feedback.value = FavoriteMenuSnackbarEvent(
                        if (menu.isFavorite) R.string.favorite_menu_deleted_snackbar else R.string.favorite_menu_added_snackbar,
                        false
                    )
                },
            )
        }.use {
            awaitText(context.getString(R.string.today_favorite_title))
            tap(awaitNode { it.className?.toString() == "android.widget.EditText" })
            awaitDescription(R.string.favorite_menu_clear_search_content_description)
            assertNull(findNode(instrumentation.uiAutomation.rootInActiveWindow) {
                it.text?.toString() == context.getString(
                    R.string.today_favorite_title
                )
            })
            assertNull(findNode(instrumentation.uiAutomation.rootInActiveWindow) {
                it.text?.toString() == context.getString(
                    R.string.favorite_menu_section_title
                )
            })
            screenshot("menu-search-focused")
            setQuery("me")
            assertEquals(0, requests.get())
            assertNull(findNode(instrumentation.uiAutomation.rootInActiveWindow) {
                it.text?.toString()?.startsWith("Meatballs") == true
            })
            instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER)
            awaitNode { it.text?.toString()?.startsWith("Meatballs") == true }
            assertEquals(1, requests.get())
            tap(awaitDescription(R.string.favorite_menu_add_content_description))
            awaitDescription(R.string.favorite_menu_remove_content_description)
            awaitText(context.getString(R.string.favorite_menu_added_snackbar))
            tap(awaitDescription(R.string.favorite_menu_remove_content_description))
            awaitDescription(R.string.favorite_menu_add_content_description)
            awaitText(context.getString(R.string.favorite_menu_deleted_snackbar))
            setQuery("zz")
            assertEquals(1, requests.get())
            instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER)
            awaitText(context.getString(R.string.favorite_menu_load_error))
            screenshot("menu-search-load-error")
            tap(awaitDescription(R.string.favorite_menu_clear_search_content_description))
            awaitText(context.getString(R.string.today_favorite_title))
            assertFalse(state.value.isMenuSearchMode)
            assertFalse(today.value.expanded)
            assertEquals("", state.value.menuSearchQuery)
        }
    }

    @Test
    fun alreadySavedMenusCountTowardTheNextSelectionAndSaveDisablesAgain() {
        val names = listOf("맑은미역국", "배추김치", "연탄불맛돈불고기", "양배추찜")
        val menus = names.mapIndexed { index, name ->
            MenuFavoriteSearchResult(
                index.toLong() + 1,
                name,
                Restaurant.HAKSIK,
                false
            )
        }
        val today = mutableStateOf(
            TodayMenuFavoriteState(
                expanded = true,
                menus = menus,
                loadState = MenuFavoriteLoadState.Success
            )
        )
        val favorites = mutableStateOf(
            menus.takeLast(2).map { FavoriteMenu(it.menuId, it.menuName, it.restaurant, false) })
        val feedback = mutableStateOf<FavoriteMenuSnackbarEvent?>(null)
        launch {
            FavoriteScreen(
                uiState = UiState.Success(
                    FavoriteState(
                        favoriteMenus = favorites.value,
                        favoriteMenuLoadState = MenuFavoriteLoadState.Success
                    )
                ),
                onBackToMap = {}, onStoreTypeSelected = {}, onPartnershipClick = {},
                todayMenuState = today.value, menuSnackbarEvent = feedback.value,
                onTodayToggleMenu = { id ->
                    today.value =
                        today.value.copy(selectedMenuIds = today.value.selectedMenuIds + id)
                },
                onTodaySave = {
                    favorites.value =
                        menus.map { FavoriteMenu(it.menuId, it.menuName, it.restaurant, false) }
                    today.value = today.value.copy(selectedMenuIds = emptySet())
                    feedback.value =
                        FavoriteMenuSnackbarEvent(R.string.favorite_menu_added_snackbar, false)
                },
            )
        }.use {
            assertFalse(actionNode(awaitText(context.getString(R.string.today_favorite_save))).isEnabled)
            tap(awaitText("맑은미역국"))
            tap(awaitText("배추김치"))
            val save = awaitText(context.getString(R.string.today_favorite_save_count, 4))
            assertTrue(actionNode(save).isEnabled)
            screenshot("today-favorite-four-selected")
            tap(save)
            awaitText(context.getString(R.string.favorite_menu_added_snackbar))
            assertFalse(actionNode(awaitText(context.getString(R.string.today_favorite_save))).isEnabled)
            screenshot("today-favorite-saved")
        }
    }

    private fun launch(content: @Composable () -> Unit): ActivityScenario<ComponentActivity> =
        ActivityScenario.launch(ComponentActivity::class.java).also { scenario ->
            scenario.onActivity { activity -> activity.enableEdgeToEdge(); activity.setContent { EatssuTheme { content() } } }
        }

    private fun setQuery(query: String) {
        val field = awaitNode { it.className?.toString() == "android.widget.EditText" }
        assertTrue(field.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, Bundle().apply {
            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, query)
        }))
        Thread.sleep(350)
    }

    private fun awaitText(text: String) = awaitNode { it.text?.toString() == text }
    private fun awaitDescription(id: Int) =
        awaitNode { it.contentDescription?.toString() == context.getString(id) }

    private fun awaitNode(predicate: (AccessibilityNodeInfo) -> Boolean): AccessibilityNodeInfo {
        repeat(60) {
            findNode(
                instrumentation.uiAutomation.rootInActiveWindow,
                predicate
            )?.let { Thread.sleep(300); return it }
            Thread.sleep(100)
        }
        error("Expected accessibility node not found")
    }

    private fun findNode(
        node: AccessibilityNodeInfo?,
        predicate: (AccessibilityNodeInfo) -> Boolean
    ): AccessibilityNodeInfo? {
        if (node == null) return null
        if (predicate(node)) return node
        for (index in 0 until node.childCount) findNode(
            node.getChild(index),
            predicate
        )?.let { return it }
        return null
    }

    private fun actionNode(node: AccessibilityNodeInfo): AccessibilityNodeInfo =
        if (node.isClickable || node.isCheckable || node.className?.toString() == "android.widget.Button") node
        else node.parent?.let(::actionNode) ?: node

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
            instrumentation.sendPointerSync(event); event.recycle()
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
