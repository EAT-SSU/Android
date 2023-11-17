package com.eatssu.android.ui.main

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPager2Adapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    // 1. ViewPager2에 연결할 Fragment 들을 생성
    val fragmentList = listOf<Fragment>(BreakfastFragment(), MenuFragment(), DinnerFragment())
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
    fun getDefaultFragmentPosition(): Int {
        // 여기에서 디폴트로 노출할 Fragment의 위치를 반환해줍니다.
        // 예를 들어, 첫 번째 Fragment를 디폴트로 설정하려면 0을 반환합니다.
        return 1
    }

    fun setMenudate(date : String){
        this.menuDate = date
        Log.d("vpdate", menuDate)
    }
}
