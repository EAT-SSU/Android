package com.eatssu.android.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.eatssu.android.R
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.eatssu.android.databinding.ActivityMainBinding
import com.eatssu.android.ui.calendar.*
import com.eatssu.android.ui.mypage.MyPageActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.prolificinteractive.materialcalendarview.*
import java.time.DayOfWeek
import java.time.LocalDate
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
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
                TemporalAdjusters.previous(
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
                 lateinit var holderSelect : CalendarAdapter.CalendarViewHolder
                 lateinit var selected : String

                for(holder in returnViewHolderList){
                    holder.binding.weekCardview.setBackgroundResource(R.drawable.ic_selector_background_white)
                    if(holder.today.equals(data.cl_date)) {
                        holderSelect = holder
                        selected = holder.today
                    }
                }

                holderSelect.binding.weekCardview.setBackgroundResource(R.drawable.selector_background_blue)

                val viewModel = ViewModelProvider(this@MainActivity)[CalendarViewModel::class.java]
                viewModel.setData(selected)

                // 1) ViewPager2 참조
                val viewPager: ViewPager2 = binding.vpMain
                val tabLayout: TabLayout = binding.tabLayout

                // 2) FragmentStateAdapter 생성 : Fragment 여러개를 ViewPager2에 연결해주는 역할
                val viewpagerFragmentAdapter = ViewPager2Adapter(this@MainActivity)

                viewpagerFragmentAdapter.setMenudate(selected)

                Log.d("todaydate", selected)

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


            }
        })

        recyclerView.adapter = adapter

        /*val monthFormat =
            DateTimeFormatter.ofPattern("yyyy.MM.dd").withLocale(Locale.forLanguageTag("ko"))
        val localDate = LocalDateTime.now().format(monthFormat)
        binding.textYearMonth.text = localDate


        binding.textYearMonth.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java);
            startActivityForResult(intent, CALENDAR_REQUEST_CODE)
        }*/

        val bundle : Bundle = Bundle()
        var calendarFragment : CalendarFragment = CalendarFragment()

        /*val year = binding.textYearMonth.text.substring(0,4)
        val month = binding.textYearMonth.text.substring(5,7)
        val day = binding.textYearMonth.text.substring(8,10)
        bundle.putString("Date", binding.textYearMonth.text.toString())

        Log.d("cutyear", year)
        Log.d("cutmonth", month)
        Log.d("cutday", day)

        binding.btnCalendarLeft.setOnClickListener {
            val currentDate = LocalDate.parse(binding.textYearMonth.text.toString(), DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            val previousDate = currentDate.minusDays(1)
            val newDate = previousDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            binding.textYearMonth.text = newDate
            bundle.putString("Date", newDate)
        }

        binding.btnCalendarRight.setOnClickListener {
            val currentDate = LocalDate.parse(binding.textYearMonth.text.toString(), DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            val previousDate = currentDate.plusDays(1)
            val newDate = previousDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            binding.textYearMonth.text = newDate
            bundle.putString("Date", newDate)
        }*/
    }


    /*@Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CALENDAR_REQUEST_CODE && resultCode == RESULT_OK) {
            val changedDate = data?.getStringExtra("changedate")
            if (changedDate != null) {
                selectedDate = changedDate
                binding.textYearMonth.text = selectedDate
                Log.d("changedate", selectedDate)
            }
        }
    }*/


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

    override fun onResume() {
        super.onResume()
    }

    /*override fun onRestart() { //여기 문제 있음
        super.onRestart()
        binding.textYearMonth.text = selectedDate
    }
*/
    companion object {
        private const val CALENDAR_REQUEST_CODE = 1
    }
}

