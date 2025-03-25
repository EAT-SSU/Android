package com.eatssu.android.presentation.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.eatssu.android.data.enums.Time
import com.eatssu.android.presentation.main.cafeteria.CafeteriaFragment
import com.eatssu.android.presentation.main.menu.MenuFragment
import com.eatssu.android.presentation.mypage.MyPageFragment

class MainViewPager2Adapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val mainFragmentList = listOf(
        CafeteriaFragment(),
        MyPageFragment()
    )

    override fun getItemCount(): Int = mainFragmentList.count()

    override fun createFragment(position: Int): Fragment {
        return mainFragmentList[position]
    }
}
