package com.eatssu.android.ui.main

import com.eatssu.android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64.NO_WRAP
import android.util.Base64.encodeToString
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.viewpager2.widget.ViewPager2
import com.eatssu.android.databinding.ActivityMainBinding
import com.eatssu.android.ui.calendar.CalendarFragment
import com.eatssu.android.ui.BaseActivity
import com.eatssu.android.ui.calendar.CalendarActivity
import com.eatssu.android.ui.main.ViewPager2Adapter
import com.eatssu.android.ui.mypage.MyPageActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import com.prolificinteractive.materialcalendarview.format.DateFormatTitleFormatter
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter
import com.prolificinteractive.materialcalendarview.format.TitleFormatter
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var selectedDate: String = ""
    private var year: String = ""
    private var month: String = ""
    private var day: String = ""

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
        //진입 시 디폴트 tab 설정 -> 나중에 시간대 별로 설정되게 수정할 것
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

        val monthFormat =
            DateTimeFormatter.ofPattern("yyyy.MM.dd").withLocale(Locale.forLanguageTag("ko"))
        val localDate = LocalDateTime.now().format(monthFormat)
        binding.textYearMonth.text = localDate


        binding.textYearMonth.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java);
            startActivityForResult(intent, CALENDAR_REQUEST_CODE)

        }
        val year = binding.textYearMonth.text.substring(0,4)
        val month = binding.textYearMonth.text.substring(5,7)
        val day = binding.textYearMonth.text.substring(8,10)
        Log.d("cutyear", year)
        Log.d("cutmonth", month)
        Log.d("cutday", day)
        binding.btnCalendarLeft.setOnClickListener {
            val currentDate = LocalDate.parse(binding.textYearMonth.text.toString(), DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            val previousDate = currentDate.minusDays(1)
            val newDate = previousDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            binding.textYearMonth.text = newDate
        }

        binding.btnCalendarRight.setOnClickListener {
            val currentDate = LocalDate.parse(binding.textYearMonth.text.toString(), DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            val previousDate = currentDate.plusDays(1)
            val newDate = previousDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
            binding.textYearMonth.text = newDate
        }
    }


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

    override fun onRestart() { //여기 문제 있음
        super.onRestart()
        binding.textYearMonth.text = selectedDate
    }

    companion object {
        private const val CALENDAR_REQUEST_CODE = 1
    }
}

