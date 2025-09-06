package com.eatssu.android.presentation.cafeteria

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.eatssu.android.databinding.FragmentCafeteriaBinding
import com.eatssu.android.presentation.MainViewModel
import com.eatssu.android.presentation.base.BaseFragment
import com.eatssu.android.presentation.cafeteria.calendar.CalendarAdapter
import com.eatssu.android.presentation.cafeteria.calendar.CalendarAdapter.OnItemListener
import com.eatssu.android.presentation.util.CalendarUtil
import com.eatssu.android.presentation.util.CalendarUtil.daysInWeekArray
import com.eatssu.android.presentation.util.CalendarUtil.monthYearFromDate
import com.eatssu.common.EventLogger
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class CafeteriaFragment : BaseFragment<FragmentCafeteriaBinding>(), OnItemListener {

    private val mainViewModel by activityViewModels<MainViewModel>()

    private var monthYearText: TextView? = null
    private var calendarRecyclerView: RecyclerView? = null
    private var mainPosition: Int = -1

    override fun setBinding(layoutInflater: LayoutInflater): FragmentCafeteriaBinding {
        return FragmentCafeteriaBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager2 = binding.vpMain
        val tabLayout: TabLayout = binding.tabLayout

        val viewpagerFragmentAdapter = CafeteriaViewPagerAdapter(requireActivity())
        viewPager.adapter = viewpagerFragmentAdapter
        viewPager.setCurrentItem(viewpagerFragmentAdapter.getDefaultFragmentPosition(), false)

        val tabTitles = listOf("아침", "점심", "저녁")
        TabLayoutMediator(tabLayout, viewPager) { tab, position -> tab.text = tabTitles[position] }.attach()

        initWidgets()
        CalendarUtil.selectedDate = LocalDate.now()
        mainViewModel.setData(CalendarUtil.selectedDate)
        setWeekView()
        setCalendarWeekClickListener()
    }

    private fun initWidgets() {
        calendarRecyclerView = binding.weekRecycler
        monthYearText = binding.monthYearTV
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setWeekView() {
        monthYearText?.text = CalendarUtil.selectedDate?.let { monthYearFromDate(it) }
        val days = CalendarUtil.selectedDate?.let { daysInWeekArray(it) }
        val calendarAdapter = days?.let { CalendarAdapter(it, this) }
        val gridLayoutManager = GridLayoutManager(requireContext(), 7)

        calendarRecyclerView?.layoutManager = gridLayoutManager
        calendarRecyclerView?.adapter = calendarAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(position: Int, date: LocalDate) {
        CalendarUtil.selectedDate = date
        mainViewModel.setData(date)
        mainPosition = position
        setWeekView()
        EventLogger.clickDay(date.dayOfWeek.name)
    }
}
