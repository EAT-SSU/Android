package com.eatssu.android.presentation.cafeteria

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.eatssu.android.R
import com.eatssu.android.databinding.FragmentCafeteriaBinding
import com.eatssu.android.presentation.MainViewModel
import com.eatssu.android.presentation.base.BaseFragment
import com.eatssu.android.presentation.cafeteria.calendar.CalendarAdapter
import com.eatssu.android.presentation.util.CalendarUtil
import com.eatssu.android.presentation.util.CalendarUtil.daysInWeekArray
import com.eatssu.android.presentation.util.CalendarUtil.monthYearFromDate
import com.eatssu.common.analytics.CafeteriaAnalyticsEvent
import com.eatssu.common.enums.ScreenId
import com.eatssu.common.enums.Time
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class CafeteriaFragment : BaseFragment<FragmentCafeteriaBinding>(
    ScreenId.HOME_MAIN
), CalendarAdapter.OnItemListener {

    private val mainViewModel by activityViewModels<MainViewModel>()

    private var monthYearText: TextView? = null
    private var calendarRecyclerView: RecyclerView? = null
    private var mainPosition: Int = -1

    override fun setBinding(layoutInflater: LayoutInflater): FragmentCafeteriaBinding {
        return FragmentCafeteriaBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager2 = binding.vpMain
        val tabLayout: TabLayout = binding.tabLayout

        val viewpagerFragmentAdapter = CafeteriaViewPagerAdapter(requireActivity())
        viewPager.adapter = viewpagerFragmentAdapter
        viewPager.setCurrentItem(viewpagerFragmentAdapter.getDefaultFragmentPosition(), false)

        val tabTitles = listOf(getString(R.string.widget_morning), getString(R.string.widget_lunch), getString(R.string.widget_dinner))
        TabLayoutMediator(tabLayout, viewPager) { tab, position -> tab.text = tabTitles[position] }.attach()

        // ViewPager 페이지 변경 감지
        setupViewPagerPageChangeListener(viewPager)

        initWidgets()
        CalendarUtil.selectedDate = LocalDate.now()
        mainViewModel.setData(CalendarUtil.selectedDate)
        setWeekView()
        setCalendarWeekClickListener()
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
