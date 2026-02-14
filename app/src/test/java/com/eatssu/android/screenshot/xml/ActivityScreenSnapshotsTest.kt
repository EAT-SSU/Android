package com.eatssu.android.screenshot.xml

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ContextThemeWrapper
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.eatssu.android.R
import com.eatssu.android.screenshot.core.ScreenshotCapture
import com.eatssu.android.screenshot.core.ScreenshotDeterminismRule
import com.eatssu.android.screenshot.core.ScreenshotTestApplication
import com.eatssu.android.screenshot.inventory.ScreenCoverageItem
import com.eatssu.android.screenshot.inventory.ScreenCoverageRegistry
import com.google.android.material.bottomnavigation.BottomNavigationView
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
class ActivityScreenSnapshotsTest {
    @get:Rule
    val determinismRule = ScreenshotDeterminismRule()

    @Test
    fun captureAllActivityScreens() {
        ScreenCoverageRegistry.itemsFor("xml/ActivityScreenSnapshotsTest.kt")
            .forEach { item ->
                item.states.forEach { state ->
                    val view = renderActivityScreen(item, state)
                    ScreenshotCapture.captureView(
                        type = "activity",
                        target = ScreenCoverageRegistry.screenshotTargetName(item.targetId),
                        state = state,
                        view = view,
                    )
                }
            }
    }

    private fun renderActivityScreen(item: ScreenCoverageItem, state: String): ViewGroup {
        val root = when (item.targetId) {
            "activity:.presentation.intro.IntroActivity" -> inflateLayout(R.layout.activity_intro)
            "activity:.presentation.login.LoginActivity" -> inflateLayout(R.layout.activity_login)
            "activity:.presentation.MainActivity" -> buildMainActivityLayout()
            "activity:.presentation.cafeteria.review.report.ReportActivity" -> inflateLayout(R.layout.activity_report)
            "activity:.presentation.common.AndroidMessageDialogActivity" -> inflateDialogLayout(R.layout.dialog_default)
            "activity:.presentation.common.ForceUpdateDialogActivity" -> inflateDialogLayout(R.layout.dialog_default)
            "activity:.presentation.mypage.DeveloperActivity" -> inflateLayout(R.layout.activity_developer)
            "activity:.presentation.mypage.SignOutActivity" -> inflateLayout(R.layout.activity_sign_out)
            "activity:.presentation.mypage.terms.WebViewActivity" -> inflateLayout(R.layout.activity_webview)
            "activity:.presentation.mypage.userinfo.UserInfoActivity" -> inflateLayout(R.layout.activity_user_info)
            else -> error("Unsupported activity target: ${item.targetId}")
        }

        applyState(root, item.targetId, state)
        attachStateBadge(root, item.targetId, state)
        return root
    }

    private fun applyState(root: ViewGroup, targetId: String, state: String) {
        when (targetId) {
            "activity:.presentation.login.LoginActivity" -> {
                val progress = root.findViewByIdOrNull<ProgressBar>(R.id.progressBar)
                val loginButton = root.findViewByIdOrNull<View>(R.id.ib_kakao_login)
                val loading = state == "loading"
                progress?.visibility = if (loading) View.VISIBLE else View.GONE
                loginButton?.visibility = if (loading) View.INVISIBLE else View.VISIBLE
            }

            "activity:.presentation.MainActivity" -> {
                val nav = root.findViewByIdOrNull<BottomNavigationView>(R.id.bottom_navi_bar)
                nav?.selectedItemId = when (state) {
                    "success_map" -> R.id.map_menu
                    "success_mypage" -> R.id.mypage_menu
                    else -> R.id.cafeteria_menu
                }
            }

            "activity:.presentation.cafeteria.review.report.ReportActivity" -> {
                val sendButton = root.findViewByIdOrNull<Button>(R.id.btn_send_report)
                val reportInput = root.findViewByIdOrNull<EditText>(R.id.et_report_comment)
                val radio1 = root.findViewByIdOrNull<RadioButton>(R.id.radio_bt1)
                when (state) {
                    "loading" -> {
                        sendButton?.isEnabled = false
                        reportInput?.setText("로딩중...")
                    }
                    "empty" -> {
                        sendButton?.isEnabled = true
                        reportInput?.setText("")
                    }
                    "success" -> {
                        sendButton?.isEnabled = true
                        radio1?.isChecked = true
                        reportInput?.setText("부적절한 리뷰입니다.")
                    }
                    "error" -> {
                        sendButton?.isEnabled = false
                        reportInput?.setText("신고 전송 실패")
                    }
                }
            }

            "activity:.presentation.mypage.SignOutActivity" -> {
                val input = root.findViewByIdOrNull<EditText>(R.id.et_enter_nickname)
                val button = root.findViewByIdOrNull<Button>(R.id.btn_sign_out)
                when (state) {
                    "empty" -> {
                        input?.setText("")
                        button?.isEnabled = false
                    }
                    "success" -> {
                        input?.setText("eatssu")
                        button?.isEnabled = true
                    }
                    "error" -> {
                        input?.setText("wrong")
                        button?.isEnabled = false
                    }
                }
            }

            "activity:.presentation.mypage.terms.WebViewActivity" -> {
                val webView = root.findViewByIdOrNull<WebView>(R.id.webview)
                val color = when (state) {
                    "loading" -> Color.parseColor("#F1F5F9")
                    "success" -> Color.parseColor("#E2E8F0")
                    else -> Color.parseColor("#FEE2E2")
                }
                webView?.setBackgroundColor(color)
            }

            "activity:.presentation.mypage.userinfo.UserInfoActivity" -> {
                val nickname = root.findViewByIdOrNull<EditText>(R.id.et_ch_nickname)
                val status = root.findViewByIdOrNull<TextView>(R.id.tv_nickname_status)
                val saveButton = root.findViewByIdOrNull<Button>(R.id.btn_complete)
                when (state) {
                    "loading" -> {
                        nickname?.setText("")
                        status?.text = "로딩중"
                        saveButton?.isEnabled = false
                    }
                    "empty" -> {
                        nickname?.setText("")
                        status?.text = "닉네임을 입력해주세요"
                        saveButton?.isEnabled = false
                    }
                    "success" -> {
                        nickname?.setText("eatssu_user")
                        status?.text = "사용 가능한 닉네임입니다"
                        saveButton?.isEnabled = true
                    }
                    "error" -> {
                        nickname?.setText("invalid!!")
                        status?.text = "닉네임 형식 오류"
                        saveButton?.isEnabled = false
                    }
                }
            }
        }
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

    private fun inflateDialogLayout(@LayoutRes layoutRes: Int): ViewGroup {
        val context = themedContext()
        val container = FrameLayout(context)
        val dialog = LayoutInflater.from(context).inflate(layoutRes, container, false)
        container.setBackgroundColor(Color.parseColor("#59000000"))
        container.addView(
            dialog,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
        )
        return container
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

    private fun buildMainActivityLayout(): ViewGroup {
        val context = themedContext()
        val container = FrameLayout(context).apply {
            setBackgroundColor(Color.WHITE)
        }

        val content = TextView(context).apply {
            text = "MainActivity Snapshot"
            textSize = 20f
            setTextColor(Color.parseColor("#111827"))
            gravity = Gravity.CENTER
        }
        container.addView(
            content,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        val nav = BottomNavigationView(context).apply {
            id = R.id.bottom_navi_bar
            menu.add(0, R.id.cafeteria_menu, 0, "식단")
            menu.add(0, R.id.map_menu, 1, "지도")
            menu.add(0, R.id.mypage_menu, 2, "마이")
            setBackgroundColor(Color.WHITE)
        }
        container.addView(
            nav,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
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
