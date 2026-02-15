package com.eatssu.android.screenshot.xml

import android.content.Context
import android.graphics.Color
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.eatssu.android.R
import com.eatssu.android.screenshot.core.ScreenshotCapture
import com.eatssu.android.screenshot.core.ScreenshotDeterminismRule
import com.eatssu.android.screenshot.core.ScreenshotTestApplication
import com.eatssu.android.screenshot.inventory.ScreenCoverageItem
import com.eatssu.android.screenshot.inventory.ScreenCoverageRegistry
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

            "fragment:com.eatssu.android.presentation.cafeteria.info.InfoBottomSheetFragment" ->
                inflateBottomSheetLayout(R.layout.fragment_bottomsheet_info)

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
                val weekRecycler = root.findViewByIdOrNull<RecyclerView>(R.id.week_recycler)
                val tabLayout = root.findViewByIdOrNull<TabLayout>(R.id.tabLayout)
                val viewPager = root.findViewByIdOrNull<ViewPager2>(R.id.vp_main)

                monthText?.text = if (state == "loading") "로딩중" else "2025. 01"

                weekRecycler?.apply {
                    layoutManager = GridLayoutManager(context, 7)
                    adapter = LabelAdapter(listOf("월", "화", "수", "목", "금", "토", "일"))
                }

                tabLayout?.apply {
                    if (tabCount == 0) {
                        addTab(newTab().setText("아침"))
                        addTab(newTab().setText("점심"))
                        addTab(newTab().setText("저녁"))
                    }
                    getTabAt(1)?.select()
                }

                viewPager?.adapter = CafeteriaPagerAdapter(state)

                if (state == "loading") {
                    attachOverlayMessage(root, "식단을 불러오는 중", showProgress = true)
                }
            }

            "fragment:com.eatssu.android.presentation.cafeteria.menu.MenuFragment" -> {
                val recyclerView = root.findViewByIdOrNull<RecyclerView>(R.id.rv)
                recyclerView?.layoutManager = LinearLayoutManager(root.context)

                when (state) {
                    "loading" -> {
                        recyclerView?.adapter = MenuSectionSnapshotAdapter(sampleMenuSections())
                        recyclerView?.alpha = 0.55f
                        attachOverlayMessage(root, "메뉴 불러오는 중", showProgress = true)
                    }

                    "empty" -> {
                        recyclerView?.adapter = MenuSectionSnapshotAdapter(emptyList())
                        attachOverlayMessage(root, "표시할 메뉴가 없습니다", showProgress = false)
                    }

                    "success" -> {
                        recyclerView?.alpha = 1f
                        recyclerView?.adapter = MenuSectionSnapshotAdapter(sampleMenuSections())
                    }

                    "error" -> {
                        recyclerView?.adapter = MenuSectionSnapshotAdapter(emptyList())
                        attachOverlayMessage(root, "메뉴 조회 실패", showProgress = false)
                    }
                }
            }

            "fragment:com.eatssu.android.presentation.mypage.MyPageFragment" -> {
                val nickname = root.findViewByIdOrNull<TextView>(R.id.tv_nickname)
                when (state) {
                    "loading" -> {
                        nickname?.text = "로딩중"
                        root.alpha = 0.96f
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

            "fragment:com.eatssu.android.presentation.cafeteria.info.InfoBottomSheetFragment" -> {
                val name = root.findViewByIdOrNull<TextView>(R.id.tv_name)
                val location = root.findViewByIdOrNull<TextView>(R.id.tv_location)
                val time = root.findViewByIdOrNull<TextView>(R.id.tv_time)
                val etc = root.findViewByIdOrNull<TextView>(R.id.tv_etc)

                when (state) {
                    "empty" -> {
                        name?.text = "정보 없음"
                        location?.text = "-"
                        time?.text = "-"
                        etc?.text = "등록된 안내가 없습니다"
                    }

                    else -> {
                        name?.text = "학생식당"
                        location?.text = "학생회관 3층"
                        time?.text = "11:20~14:00"
                        etc?.text = "돈까스/한식 코너 운영"
                    }
                }
            }
        }
    }

    private fun attachOverlayMessage(root: ViewGroup, message: String, showProgress: Boolean) {
        val context = root.context
        val container = FrameLayout(context).apply {
            setBackgroundColor(Color.parseColor("#66FFFFFF"))
            tag = "snapshot-overlay"
        }

        (root.findViewWithTag<View>("snapshot-overlay") as? View)?.let { existing ->
            root.removeView(existing)
        }

        if (showProgress) {
            val progress = ProgressBar(context)
            container.addView(
                progress,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER,
                ),
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
                Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM,
            ).apply {
                bottomMargin = 120
            },
        )

        root.addView(
            container,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            ),
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

    private fun inflateBottomSheetLayout(@LayoutRes layoutRes: Int): ViewGroup {
        val context = themedContext()
        val container = FrameLayout(context).apply {
            setBackgroundColor(Color.parseColor("#59000000"))
        }
        val sheet = LayoutInflater.from(context).inflate(layoutRes, container, false)
        container.addView(
            sheet,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM,
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

    private fun sampleMenuSections(): List<MenuSectionSnapshot> {
        return listOf(
            MenuSectionSnapshot(
                cafeteria = "학생 식당",
                location = "학생회관 3층",
                menus = listOf(
                    MenuRowSnapshot("돈까스", "5,500", "4.3"),
                    MenuRowSnapshot("미역국", "1,000", "4.1"),
                ),
            ),
            MenuSectionSnapshot(
                cafeteria = "도담 식당",
                location = "레지던스홀 1층",
                menus = listOf(
                    MenuRowSnapshot("제육볶음", "6,000", "4.5"),
                    MenuRowSnapshot("김치찌개", "5,000", "4.0"),
                ),
            ),
        )
    }

    private fun <T : View> View.findViewByIdOrNull(id: Int): T? {
        return try {
            findViewById(id)
        } catch (_: Throwable) {
            null
        }
    }

    private data class MenuSectionSnapshot(
        val cafeteria: String,
        val location: String,
        val menus: List<MenuRowSnapshot>,
    )

    private data class MenuRowSnapshot(
        val menu: String,
        val price: String,
        val rate: String,
    )

    private class MenuSectionSnapshotAdapter(
        private val sections: List<MenuSectionSnapshot>,
    ) : RecyclerView.Adapter<MenuSectionSnapshotAdapter.SectionViewHolder>() {
        class SectionViewHolder(val root: View) : RecyclerView.ViewHolder(root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cafeteria_section, parent, false)
            return SectionViewHolder(view)
        }

        override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
            val section = sections[position]
            holder.root.findViewById<TextView>(R.id.tv_cafeteria).text = section.cafeteria
            holder.root.findViewById<TextView>(R.id.tv_cafeteria_location).text = section.location

            holder.root.findViewById<RecyclerView>(R.id.rv_menu).apply {
                layoutManager = LinearLayoutManager(context)
                adapter = MenuRowSnapshotAdapter(section.menus)
            }
        }

        override fun getItemCount(): Int = sections.size
    }

    private class MenuRowSnapshotAdapter(
        private val menus: List<MenuRowSnapshot>,
    ) : RecyclerView.Adapter<MenuRowSnapshotAdapter.MenuViewHolder>() {
        class MenuViewHolder(val root: View) : RecyclerView.ViewHolder(root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_menu, parent, false)
            return MenuViewHolder(view)
        }

        override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
            val row = menus[position]
            holder.root.findViewById<TextView>(R.id.tv_menu).text = row.menu
            holder.root.findViewById<TextView>(R.id.tv_price).text = row.price
            holder.root.findViewById<TextView>(R.id.tv_rate).text = row.rate
        }

        override fun getItemCount(): Int = menus.size
    }

    private class CafeteriaPagerAdapter(
        private val state: String,
    ) : RecyclerView.Adapter<CafeteriaPagerAdapter.PageViewHolder>() {
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
            holder.container.removeAllViews()
            val page = LayoutInflater.from(holder.container.context)
                .inflate(R.layout.fragment_menu, holder.container, false)

            val recycler = page.findViewById<RecyclerView>(R.id.rv)
            recycler.layoutManager = LinearLayoutManager(page.context)
            recycler.adapter = MenuSectionSnapshotAdapter(
                if (state == "loading") emptyList() else sampleSectionsForPage(position),
            )

            holder.container.addView(
                page,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                ),
            )
        }

        override fun getItemCount(): Int = 3

        private fun sampleSectionsForPage(position: Int): List<MenuSectionSnapshot> {
            val cafeteria = when (position) {
                0 -> "아침 메뉴"
                1 -> "점심 메뉴"
                else -> "저녁 메뉴"
            }
            return listOf(
                MenuSectionSnapshot(
                    cafeteria = cafeteria,
                    location = "학생회관",
                    menus = listOf(
                        MenuRowSnapshot("돈까스", "5,500", "4.3"),
                        MenuRowSnapshot("김치찌개", "5,000", "4.1"),
                    ),
                ),
            )
        }
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
