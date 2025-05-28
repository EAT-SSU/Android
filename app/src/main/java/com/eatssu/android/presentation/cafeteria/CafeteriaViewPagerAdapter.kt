package com.eatssu.android.presentation.cafeteria

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.data.enums.Time
import com.eatssu.android.presentation.cafeteria.menu.MenuFragment
import java.time.LocalTime

class CafeteriaViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val fragmentList = listOf(
        MenuFragment.newInstance(Time.MORNING),
        MenuFragment.newInstance(Time.LUNCH),
        MenuFragment.newInstance(Time.DINNER)
    )

    lateinit var menuDate : String

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun getDefaultFragmentPosition(context: Context): Int {
        val savedPosition = MySharedPreferences.getPreTimePosition(context)
        return if (savedPosition in 0..2) savedPosition else {
            // fallback: 시간 기준
            val time = LocalTime.now()
            when (time.hour) {
                in 0..9 -> 0
                in 10..15 -> 1
                in 16..23 -> 2
                else -> 1
            }
        }
    }
}
