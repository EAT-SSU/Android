package com.eatssu.android.view.main

import com.eatssu.android.R
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.eatssu.android.adapter.CalendarAdapter
import com.eatssu.android.adapter.OnItemClickListener
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.data.NetworkConnection
import com.eatssu.android.data.entity.CalendarData
import com.eatssu.android.databinding.ActivityMainBinding
import com.eatssu.android.view.mypage.ChangeNicknameActivity
import com.eatssu.android.view.mypage.MyPageActivity
import com.eatssu.android.viewmodel.CalendarViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.prolificinteractive.materialcalendarview.*
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var selectedDate: String = ""
    private var year: String = ""
    private var month: String = ""
    private var day: String = ""
    lateinit var calendarAdapter: CalendarAdapter
    private var calendarList = ArrayList<CalendarData>()
    private var allViewHolders : List<CalendarAdapter.CalendarViewHolder> = mutableListOf()

    private val networkCheck: NetworkConnection by lazy {
        NetworkConnection(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        networkCheck.register() // 네트워크 객체 등록

        val intentNick = Intent(this, ChangeNicknameActivity::class.java)
        // SharedPreferences 안에 값이 저장되어 있지 않을 때 -> Login
        if (MySharedPreferences.getUserName(this@MainActivity).isBlank()) {
            startActivity(intentNick)
        }

        supportActionBar?.title = "EAT-SSU"

        // 1) ViewPager2 참조
        val viewPager: ViewPager2 = binding.vpMain
        val tabLayout: TabLayout = binding.tabLayout

        // 2) FragmentStateAdapter 생성 : Fragment 여러개를 ViewPager2에 연결해주는 역할
        val viewpagerFragmentAdapter = ViewPager2Adapter(this)

        // 3) ViewPager2의 adapter에 설정
        viewPager.adapter = viewpagerFragmentAdapter
        viewPager.setCurrentItem(viewpagerFragmentAdapter.getDefaultFragmentPosition(), false)


        // ###### TabLayout과 ViewPager2를 연결
        // 1. 탭메뉴의 이름을 리스트로 생성해둔다.
        val tabTitles = listOf<String>("아침", "점심", "저녁")

        // 2. TabLayout과 ViewPager2를 연결하고, TabItem의 메뉴명을 설정한다.
        TabLayoutMediator(tabLayout,
            viewPager,
            { tab, position -> tab.text = tabTitles[position] }).attach()

        binding.btnSetting.setOnClickListener() {
            val intent = Intent(this, MyPageActivity::class.java)  // 인텐트를 생성해줌,
            startActivity(intent)  // 화면 전환을 시켜줌
        }

        var week_day: Array<String> = resources.getStringArray(R.array.calendar_day)

        calendarAdapter = CalendarAdapter(calendarList)

        calendarList.apply {
            val dateFormat =
                DateTimeFormatter.ofPattern("dd").withLocale(Locale.forLanguageTag("ko"))
            val monthFormat = DateTimeFormatter.ofPattern("yyyy . MM . dd")
                .withLocale(Locale.forLanguageTag("ko"))

            val localDate = LocalDateTime.now().format(monthFormat)

            val preSunday: LocalDateTime = LocalDateTime.now().with(
                TemporalAdjusters.previousOrSame(
                    DayOfWeek.SUNDAY
                )
            )
            Log.d("preSunday", preSunday.toString())
            for (i in 0..6) {
                Log.d("날짜만", week_day[i])

                calendarList.apply {
                    add(
                        CalendarData(
                            preSunday.plusDays(i.toLong()).format(dateFormat),
                            week_day[i]
                        )
                    )
                }
                Log.d("저번 주 일요일 기준으로 시작!", preSunday.plusDays(i.toLong()).format(dateFormat))
            }
            binding.weekRecycler.adapter = calendarAdapter
            binding.weekRecycler.layoutManager = GridLayoutManager(this@MainActivity, 7)

        }

        //RecyclerView에 목록 출력
        val recyclerView = binding.weekRecycler

        val adapter = calendarAdapter

        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(v: View?, data: CalendarData) {

                val returnViewHolderList = calendarAdapter.returnViewHolderList()
                lateinit var holderSelect: CalendarAdapter.CalendarViewHolder
                lateinit var selected: String

                for (holder in returnViewHolderList) {
                    holder.binding.weekCardview.setBackgroundResource(R.drawable.ic_selector_background_white)
                    holder.binding.day.isSelected = false
                    holder.binding.date.isSelected = false
                    if (holder.today.equals(data.cl_date)) {
                        holderSelect = holder
                        selected = holder.today
                    }
                }
                holderSelect.binding.day.isSelected = true
                holderSelect.binding.date.isSelected = true
                holderSelect.binding.weekCardview.setBackgroundResource(R.drawable.transparent_calendar_element)

                val viewModel = ViewModelProvider(this@MainActivity)[CalendarViewModel::class.java]
                viewModel.setData(selected)
                // viewModel에 값 넘어가서 메뉴 뜨는지 확인하는 코드
                //var senddate = "14"
                //viewModel.setData(senddate)

                // 1) ViewPager2 참조
                val viewPager: ViewPager2 = binding.vpMain
                val tabLayout: TabLayout = binding.tabLayout

                // 2) FragmentStateAdapter 생성 : Fragment 여러개를 ViewPager2에 연결해주는 역할
                val viewpagerFragmentAdapter = ViewPager2Adapter(this@MainActivity)

                viewpagerFragmentAdapter.setMenudate(selected)

                Log.d("todaydate", selected)

                // 3) ViewPager2의 adapter에 설정
                viewPager.adapter = viewpagerFragmentAdapter
                viewPager.setCurrentItem(
                    viewpagerFragmentAdapter.getDefaultFragmentPosition(),
                    false
                )

                // ###### TabLayout과 ViewPager2를 연결
                // 1. 탭메뉴의 이름을 리스트로 생성해둔다.
                val tabTitles = listOf<String>("아침", "점심", "저녁")

                // 2. TabLayout과 ViewPager2를 연결하고, TabItem의 메뉴명을 설정한다.
                TabLayoutMediator(tabLayout,
                    viewPager,
                    { tab, position -> tab.text = tabTitles[position] }).attach()


            }
        })

        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_setting -> {
                val intent = Intent(this, MyPageActivity::class.java)  // 인텐트를 생성해줌,
                startActivity(intent)  // 화면 전환을 시켜줌
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        networkCheck.unregister() // 네트워크 객체 해제
    }
}

