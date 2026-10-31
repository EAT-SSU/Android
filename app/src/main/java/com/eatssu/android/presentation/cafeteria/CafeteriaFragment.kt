package com.eatssu.android.presentation.cafeteria

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.eatssu.android.R
import com.eatssu.android.data.local.NotificationStore
import com.eatssu.android.databinding.FragmentCafeteriaBinding
import com.eatssu.android.presentation.MainViewModel
import com.eatssu.android.presentation.base.BaseFragment
import com.eatssu.android.presentation.cafeteria.calendar.CalendarAdapter
import com.eatssu.android.presentation.notification.NotificationActivity
import com.eatssu.android.presentation.util.CalendarUtil
import com.eatssu.android.presentation.util.CalendarUtil.daysInWeekArray
import com.eatssu.android.presentation.util.CalendarUtil.monthYearFromDate
import com.eatssu.common.analytics.CafeteriaAnalyticsEvent
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.ScreenId
import com.eatssu.common.enums.Time
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class CafeteriaFragment : BaseFragment<FragmentCafeteriaBinding>(
    ScreenId.HOME_MAIN
), CalendarAdapter.OnItemListener {

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val scrollViewModel by viewModels<CafeteriaScrollViewModel>()

    @Inject
    lateinit var notificationStore: NotificationStore

    private val notificationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val date = result.data?.getStringExtra(NotificationActivity.EXTRA_MEAL_DATE)
                    ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                val time = result.data?.getStringExtra(NotificationActivity.EXTRA_MEAL_TIME)
                    ?.let { value -> Time.entries.firstOrNull { it.name == value } }
                if (date != null && time != null) {
                    onItemClick(-1, date)
                    binding.vpMain.setCurrentItem(Time.entries.indexOf(time), false)
                    val restaurant =
                        result.data?.getStringExtra(NotificationActivity.EXTRA_RESTAURANT)
                            ?.let { value -> Restaurant.entries.firstOrNull { it.name == value } }
                    scrollViewModel.request(date, time, restaurant)
                } else if (result.data?.getBooleanExtra(
                        NotificationActivity.EXTRA_OPEN_CAFETERIA,
                        false
                    ) == true
                ) {
                    val today = LocalDate.now()
                    onItemClick(-1, today)
                    val adapter = binding.vpMain.adapter as? CafeteriaViewPagerAdapter
                    val position = adapter?.getDefaultFragmentPosition() ?: 1
                    binding.vpMain.setCurrentItem(position, false)
                    scrollViewModel.request(today, Time.entries[position], Restaurant.HAKSIK)
                }
            }
        }

    private var monthYearText: TextView? = null
    private var calendarRecyclerView: RecyclerView? = null
    private var mainPosition: Int = -1
    private var selectedMealTime: Time = Time.LUNCH

    override fun setBinding(layoutInflater: LayoutInflater): FragmentCafeteriaBinding {
        return FragmentCafeteriaBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager2 = binding.vpMain
        val tabLayout: TabLayout = binding.tabLayout

        val viewpagerFragmentAdapter = CafeteriaViewPagerAdapter(this)
        viewPager.adapter = viewpagerFragmentAdapter
        viewPager.setCurrentItem(viewpagerFragmentAdapter.getDefaultFragmentPosition(), false)

        val tabTitles = listOf(getString(R.string.widget_morning), getString(R.string.widget_lunch), getString(R.string.widget_dinner))
        TabLayoutMediator(tabLayout, viewPager) { tab, position -> tab.text = tabTitles[position] }.attach()

        // ViewPager 페이지 변경 감지
        setupViewPagerPageChangeListener(viewPager)

        initWidgets()
        val selectedState = savedInstanceState ?: arguments
        CalendarUtil.selectedDate = selectedState?.getString(NotificationActivity.EXTRA_MEAL_DATE)
            ?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: LocalDate.now()
        selectedState?.getString(NotificationActivity.EXTRA_MEAL_TIME)
            ?.let { value -> Time.entries.firstOrNull { it.name == value } }
            ?.let { viewPager.setCurrentItem(Time.entries.indexOf(it), false) }
        mainViewModel.setData(CalendarUtil.selectedDate)
        val targetRestaurant = selectedState?.getString(NotificationActivity.EXTRA_RESTAURANT)
            ?.let { value -> Restaurant.entries.firstOrNull { it.name == value } }
        scrollViewModel.request(
            CalendarUtil.selectedDate,
            Time.entries[viewPager.currentItem],
            targetRestaurant
        )
        setWeekView()
        setCalendarWeekClickListener()
        binding.btnNotifications.setOnClickListener {
            notificationLauncher.launch(Intent(requireContext(), NotificationActivity::class.java))
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                notificationStore.notifications.collect { notifications ->
                    binding.btnNotifications.setImageResource(
                        if (notifications.any { !it.isRead }) R.drawable.ic_alarm_unread else R.drawable.ic_alarm_read,
                    )
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(
            NotificationActivity.EXTRA_MEAL_DATE,
            CalendarUtil.selectedDate.toString()
        )
        outState.putString(NotificationActivity.EXTRA_MEAL_TIME, selectedMealTime.name)
        outState.putString(
            NotificationActivity.EXTRA_RESTAURANT,
            scrollViewModel.target.value?.restaurant?.name
        )
    }

    private fun setupViewPagerPageChangeListener(viewPager: ViewPager2) {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // 페이지 변경 시 이벤트 로깅
                val time = when (position) {
                    0 -> Time.MORNING
                    1 -> Time.LUNCH
                    2 -> Time.DINNER
                    else -> Time.LUNCH // 기본값
                }
                selectedMealTime = time
                analyticsTracker.track(CafeteriaAnalyticsEvent.MealTimeSelected(time))
            }
        })
    }

    private fun initWidgets() {
        calendarRecyclerView = binding.weekRecycler
        monthYearText = binding.monthYearTV
    }

    private fun setWeekView() {
        monthYearText?.text = monthYearFromDate(CalendarUtil.selectedDate)
        val days = daysInWeekArray(CalendarUtil.selectedDate)
        val calendarAdapter = CalendarAdapter(days, this)
        val gridLayoutManager = GridLayoutManager(requireContext(), 7)

        calendarRecyclerView?.layoutManager = gridLayoutManager
        calendarRecyclerView?.adapter = calendarAdapter
    }

    fun setCalendarWeekClickListener() {
        binding.btnPreviousWeek.setOnClickListener {
            CalendarUtil.selectedDate = CalendarUtil.selectedDate.minusWeeks(1)
            onItemClick(mainPosition, CalendarUtil.selectedDate)
            setWeekView()
        }

        binding.btnNextWeek.setOnClickListener {
            CalendarUtil.selectedDate = CalendarUtil.selectedDate.plusWeeks(1)
            onItemClick(mainPosition, CalendarUtil.selectedDate)
            setWeekView()
        }
    }

    override fun onItemClick(position: Int, date: LocalDate) {
        CalendarUtil.selectedDate = date
        mainViewModel.setData(date)
        mainPosition = position
        setWeekView()
        analyticsTracker.track(CafeteriaAnalyticsEvent.DaySelected(date.dayOfWeek.name))
    }
}
