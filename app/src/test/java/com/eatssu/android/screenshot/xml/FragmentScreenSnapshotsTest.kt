package com.eatssu.android.screenshot.xml

import android.graphics.Color
import android.view.Gravity
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.R
import com.eatssu.android.screenshot.core.ScreenshotCapture
import com.eatssu.android.screenshot.core.ScreenshotDeterminismRule
import com.eatssu.android.screenshot.core.ScreenshotTestApplication
import com.eatssu.android.screenshot.inventory.ScreenCoverageItem
import com.eatssu.android.screenshot.inventory.ScreenCoverageRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    application = ScreenshotTestApplication::class,
    sdk = [35],
    qualifiers = "ko-rKR-w411dp-h891dp-xxhdpi",
)
class FragmentScreenSnapshotsTest {
    @get:Rule
    val determinismRule = ScreenshotDeterminismRule()

    @Test
    fun captureAllFragmentScreens() {
        ScreenCoverageRegistry.itemsFor("xml/FragmentScreenSnapshotsTest.kt")
            .forEach { item ->
                item.states.forEach { state ->
                    val view = renderFragmentScreen(item, state)
                    ScreenshotCapture.captureView(
                        type = "fragment",
                        target = ScreenCoverageRegistry.screenshotTargetName(item.targetId),
                        state = state,
                        view = view,
                    )
                }
            }
    }

    private fun renderFragmentScreen(item: ScreenCoverageItem, state: String): ViewGroup {
        val root = when (item.targetId) {
            "fragment:com.eatssu.android.presentation.cafeteria.CafeteriaFragment" ->
                inflateLayout(R.layout.fragment_cafeteria)

            "fragment:com.eatssu.android.presentation.cafeteria.menu.MenuFragment" ->
                inflateLayout(R.layout.fragment_menu)

            "fragment:com.eatssu.android.presentation.mypage.MyPageFragment" ->
                inflateLayout(R.layout.fragment_my_page)

            else -> error("Unsupported fragment target: ${item.targetId}")
        }

        applyState(root, item.targetId, state)
        attachStateBadge(root, item.targetId, state)
        return root
    }

    private fun applyState(root: ViewGroup, targetId: String, state: String) {
        when (targetId) {
            "fragment:com.eatssu.android.presentation.cafeteria.CafeteriaFragment" -> {
                val monthText = root.findViewByIdOrNull<TextView>(R.id.monthYearTV)
                val loading = state == "loading"
                monthText?.text = if (loading) "로딩중" else "2025. 01"
                root.setBackgroundColor(if (loading) Color.parseColor("#F8FAFC") else Color.WHITE)
            }

            "fragment:com.eatssu.android.presentation.cafeteria.menu.MenuFragment" -> {
                val recyclerView = root.findViewByIdOrNull<RecyclerView>(R.id.rv)
                when (state) {
                    "loading" -> {
                        recyclerView?.visibility = View.INVISIBLE
                        attachOverlayMessage(root, "메뉴 불러오는 중", true)
                    }

                    "empty" -> {
                        recyclerView?.visibility = View.INVISIBLE
                        attachOverlayMessage(root, "표시할 메뉴가 없습니다", false)
                    }

                    "success" -> {
                        recyclerView?.visibility = View.VISIBLE
                        attachOverlayMessage(root, "점심: 돈까스, 미역국", false)
                    }

                    "error" -> {
                        recyclerView?.visibility = View.INVISIBLE
                        attachOverlayMessage(root, "메뉴 조회 실패", false)
                    }
                }
            }

            "fragment:com.eatssu.android.presentation.mypage.MyPageFragment" -> {
                val nickname = root.findViewByIdOrNull<TextView>(R.id.tv_nickname)
                when (state) {
                    "loading" -> {
                        nickname?.text = "로딩중"
                        root.alpha = 0.95f
                    }

                    "empty" -> {
                        nickname?.text = "닉네임 없음"
                        root.alpha = 1f
                    }

                    "success" -> {
                        nickname?.text = "eatssu_user"
                        root.alpha = 1f
                    }

                    "error" -> {
                        nickname?.text = "불러오기 실패"
                        root.alpha = 1f
                    }
                }
            }
        }
    }

    private fun attachOverlayMessage(root: ViewGroup, message: String, loading: Boolean) {
        val context = root.context
        val container = FrameLayout(context).apply {
            setBackgroundColor(Color.parseColor("#66FFFFFF"))
        }
        if (loading) {
            val progress = ProgressBar(context)
            container.addView(
                progress,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER
                )
            )
        }
        val text = TextView(context).apply {
            this.text = message
            setTextColor(Color.parseColor("#0F172A"))
            textSize = 16f
            setPadding(20, 20, 20, 20)
            setBackgroundColor(Color.parseColor("#CCFFFFFF"))
        }
        container.addView(
            text,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
            ).apply {
                bottomMargin = 120
            }
        )

        root.addView(
            container,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun attachStateBadge(root: ViewGroup, targetId: String, state: String) {
        val context = root.context
        val badge = TextView(context).apply {
            text = "${targetId.substringAfter(':')} :: $state"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#CC111827"))
            textSize = 12f
            setPadding(16, 10, 16, 10)
        }
        root.addView(
            badge,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP or Gravity.START
            )
        )
    }

    private fun inflateLayout(@LayoutRes layoutRes: Int): ViewGroup {
        val context = themedContext()
        val container = FrameLayout(context)
        val content = LayoutInflater.from(context).inflate(layoutRes, container, false)
        container.addView(
            content,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        return container
    }

    private fun themedContext(): ContextThemeWrapper {
        val applicationContext = RuntimeEnvironment.getApplication()
        return ContextThemeWrapper(applicationContext, R.style.Theme_EatSSUAndroid)
    }

    private fun <T : View> View.findViewByIdOrNull(id: Int): T? {
        return try {
            findViewById(id)
        } catch (_: Throwable) {
            null
        }
    }
}
