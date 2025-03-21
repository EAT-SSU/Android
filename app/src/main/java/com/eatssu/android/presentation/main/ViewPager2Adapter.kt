package com.eatssu.android.presentation.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.eatssu.android.data.enums.Time
import com.eatssu.android.presentation.main.menu.MenuFragment
import java.time.LocalTime

class ViewPager2Adapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    // 1. ViewPager2에 연결할 Fragment 들을 생성
    private val fragmentList = listOf(
        MenuFragment.newInstance(Time.MORNING),
        MenuFragment.newInstance(Time.LUNCH),
        MenuFragment.newInstance(Time.DINNER)
    )

    lateinit var menuDate : String

    // 2. ViesPager2에서 노출시킬 Fragment 의 갯수 설정
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    // 3. ViewPager2의 각 페이지에서 노출할 Fragment 설정
    override fun createFragment(position: Int): Fragment {
        /*if(fragmentList[position] is LunchFragment) {
            (fragmentList[position] as LunchFragment).setDate(menuDate)
        }*/
        return fragmentList[position]
    }

    // 4. 디폴트로 노출할 Fragment의 위치를 설정
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDefaultFragmentPosition(): Int {
        // 여기에서 디폴트로 노출할 Fragment의 위치를 반환해줍니다.
        // 예를 들어, 첫 번째 Fragment를 디폴트로 설정하려면 0을 반환합니다.

        val time = LocalTime.now()

        val selectedIndex: Int = when (time.hour) {
            in 0..9 -> 0 //아침
            in 10..15 -> 1 //점심
            in 16..24 -> 2 //저녁
            else -> 1
        }
        return selectedIndex
    }
}
