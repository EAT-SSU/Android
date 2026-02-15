package com.eatssu.android.screenshot.xml

import android.content.Context
import android.graphics.Color
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.eatssu.android.R
import com.eatssu.android.screenshot.core.ScreenshotCapture
import com.eatssu.android.screenshot.core.ScreenshotDeterminismRule
import com.eatssu.android.screenshot.core.ScreenshotTestApplication
import com.eatssu.android.screenshot.inventory.ScreenCoverageItem
import com.eatssu.android.screenshot.inventory.ScreenCoverageRegistry
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
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
            "activity:.presentation.MainActivity" -> buildMainActivityShell(state)
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
                attachBottomHint(root, "WebView deterministic mode: $state")
            }

            "activity:.presentation.mypage.userinfo.UserInfoActivity" -> {
                val nickname = root.findViewByIdOrNull<EditText>(R.id.et_ch_nickname)
                val status = root.findViewByIdOrNull<TextView>(R.id.tv_nickname_status)
                val saveButton = root.findViewByIdOrNull<Button>(R.id.btn_complete)
                val college = root.findViewByIdOrNull<TextView>(R.id.tv_college)
                val department = root.findViewByIdOrNull<TextView>(R.id.tv_department)
                when (state) {
                    "loading" -> {
                        nickname?.setText("")
                        status?.text = "로딩중"
                        college?.text = "단과대"
                        department?.text = "학과"
                        saveButton?.isEnabled = false
                    }
                    "empty" -> {
                        nickname?.setText("")
                        status?.text = "닉네임을 입력해주세요"
                        college?.text = "단과대"
                        department?.text = "학과"
                        saveButton?.isEnabled = false
                    }
                    "success" -> {
                        nickname?.setText("eatssu_user")
                        status?.text = "사용 가능한 닉네임입니다"
                        college?.text = "IT대학"
                        department?.text = "컴퓨터학부"
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

    private fun buildMainActivityShell(state: String): ViewGroup {
        val context = themedContext()
        val root = FrameLayout(context).apply {
            setBackgroundColor(Color.WHITE)
        }
        val content = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
        }

        val topContent = when (state) {
            "success_map" -> buildMapHomeContent(context)
            "success_mypage" -> buildMyPageHomeContent(context)
            else -> buildCafeteriaHomeContent(context)
        }

        content.addView(
            topContent,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f,
            ),
        )

        val bottomNav = BottomNavigationView(context).apply {
            id = R.id.bottom_navi_bar
            inflateMenu(R.menu.menu_bottom_navigation)
            itemIconTintList = null
            setBackgroundColor(Color.WHITE)
            selectedItemId = when (state) {
                "success_map" -> R.id.map_menu
                "success_mypage" -> R.id.mypage_menu
                else -> R.id.cafeteria_menu
            }
        }
        content.addView(
            bottomNav,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(context, 60),
            ),
        )

        root.addView(
            content,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            ),
        )

        return root
    }

    private fun buildCafeteriaHomeContent(context: Context): View {
        val content = LayoutInflater.from(context).inflate(R.layout.fragment_cafeteria, null, false)
        content.setBackgroundColor(Color.WHITE)

        content.findViewByIdOrNull<TextView>(R.id.monthYearTV)?.text = "2025. 01"
        content.findViewByIdOrNull<TabLayout>(R.id.tabLayout)?.apply {
            if (tabCount == 0) {
                addTab(newTab().setText("아침"))
                addTab(newTab().setText("점심"))
                addTab(newTab().setText("저녁"))
            }
            getTabAt(1)?.select()
        }

        content.findViewByIdOrNull<RecyclerView>(R.id.week_recycler)?.apply {
            layoutManager = GridLayoutManager(context, 7)
            adapter = LabelAdapter(listOf("월", "화", "수", "목", "금", "토", "일"))
        }

        content.findViewByIdOrNull<ViewPager2>(R.id.vp_main)?.apply {
            adapter = MainHomePagerAdapter(
                listOf(
                    HomePageModel("학생 식당", listOf("돈까스 5,500", "미역국 1,000")),
                    HomePageModel("도담 식당", listOf("제육볶음 6,000", "김치찌개 5,000")),
                    HomePageModel("기숙사 식당", listOf("치킨마요 4,500", "우동 3,500")),
                ),
            )
            setCurrentItem(1, false)
        }

        return content
    }

    private fun buildMapHomeContent(context: Context): View {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(dp(context, 24), dp(context, 24), dp(context, 24), dp(context, 24))
        }

        val title = TextView(context).apply {
            text = "제휴 지도"
            setTextColor(Color.parseColor("#111827"))
            textSize = 22f
        }
        root.addView(title)

        val subtitle = TextView(context).apply {
            text = "학과 필터: 컴퓨터학부"
            setTextColor(Color.parseColor("#475569"))
            textSize = 14f
        }
        root.addView(subtitle)

        val mapPlaceholder = FrameLayout(context).apply {
            setBackgroundColor(Color.parseColor("#E6EEF8"))
        }
        val marker = TextView(context).apply {
            text = "MAP TEST MODE"
            setTextColor(Color.parseColor("#334155"))
            textSize = 18f
            gravity = Gravity.CENTER
        }
        mapPlaceholder.addView(
            marker,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            ),
        )

        root.addView(
            mapPlaceholder,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f,
            ).apply {
                topMargin = dp(context, 16)
            },
        )

        return root
    }

    private fun buildMyPageHomeContent(context: Context): View {
        val content = LayoutInflater.from(context).inflate(R.layout.fragment_my_page, null, false)
        content.setBackgroundColor(Color.WHITE)
        content.findViewByIdOrNull<TextView>(R.id.tv_nickname)?.text = "eatssu_user"
        return content
    }

    private fun attachBottomHint(root: ViewGroup, text: String) {
        val hint = TextView(root.context).apply {
            this.text = text
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#CC0F172A"))
            textSize = 12f
            setPadding(16, 10, 16, 10)
        }

        root.addView(
            hint,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM or Gravity.END,
            ).apply {
                rightMargin = 16
                bottomMargin = 16
            },
        )
    }

    private fun attachStateBadge(root: ViewGroup, targetId: String, state: String) {
        if (System.getProperty("eatssu.screenshot.badge") != "true") return

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
                Gravity.TOP or Gravity.START,
            ),
        )
    }

    private fun inflateDialogLayout(@LayoutRes layoutRes: Int): ViewGroup {
        val context = themedContext()
        val container = FrameLayout(context).apply {
            setBackgroundColor(Color.parseColor("#59000000"))
        }
        val dialog = LayoutInflater.from(context).inflate(layoutRes, container, false)
        container.addView(
            dialog,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER,
            ),
        )
        return container
    }

    private fun inflateLayout(@LayoutRes layoutRes: Int): ViewGroup {
        val context = themedContext()
        val container = FrameLayout(context).apply {
            setBackgroundColor(Color.WHITE)
        }
        val content = LayoutInflater.from(context).inflate(layoutRes, container, false)
        container.addView(
            content,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            ),
        )
        return container
    }

    private fun themedContext(): ContextThemeWrapper {
        val applicationContext = RuntimeEnvironment.getApplication()
        return ContextThemeWrapper(applicationContext, R.style.Theme_EatSSUAndroid)
    }

    private fun dp(context: Context, value: Int): Int {
        return (value * context.resources.displayMetrics.density).toInt()
    }

    private fun <T : View> View.findViewByIdOrNull(id: Int): T? {
        return try {
            findViewById(id)
        } catch (_: Throwable) {
            null
        }
    }

    private data class HomePageModel(
        val cafeteria: String,
        val menus: List<String>,
    )

    private class MainHomePagerAdapter(
        private val pages: List<HomePageModel>,
    ) : RecyclerView.Adapter<MainHomePagerAdapter.PageViewHolder>() {
        class PageViewHolder(val container: FrameLayout) : RecyclerView.ViewHolder(container)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
            val container = FrameLayout(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                setBackgroundColor(Color.parseColor("#F8FAFC"))
            }
            return PageViewHolder(container)
        }

        override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
            val model = pages[position]
            holder.container.removeAllViews()

            val column = LinearLayout(holder.container.context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(32, 28, 32, 28)
                setBackgroundColor(Color.WHITE)
            }

            val title = TextView(holder.container.context).apply {
                text = model.cafeteria
                setTextColor(Color.parseColor("#111827"))
                textSize = 18f
            }
            column.addView(title)

            model.menus.forEach { menu ->
                val row = TextView(holder.container.context).apply {
                    text = menu
                    setTextColor(Color.parseColor("#334155"))
                    textSize = 14f
                    setPadding(0, 12, 0, 0)
                }
                column.addView(row)
            }

            holder.container.addView(
                column,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                ).apply {
                    leftMargin = 24
                    rightMargin = 24
                    topMargin = 16
                    bottomMargin = 16
                },
            )
        }

        override fun getItemCount(): Int = pages.size
    }

    private class LabelAdapter(
        private val labels: List<String>,
    ) : RecyclerView.Adapter<LabelAdapter.LabelViewHolder>() {
        class LabelViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
            val textView = TextView(parent.context).apply {
                gravity = Gravity.CENTER
                setTextColor(Color.parseColor("#475569"))
                textSize = 12f
                setPadding(10, 10, 10, 10)
            }
            return LabelViewHolder(textView)
        }

        override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
            holder.textView.text = labels[position]
        }

        override fun getItemCount(): Int = labels.size
    }
}
