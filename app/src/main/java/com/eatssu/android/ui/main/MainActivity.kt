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
        //viewPager.setCurrentItem(viewpagerFragmentAdapter.getDefaultFragmentPosition(), false)


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

        // Retrieve the selected date from the saved instance state, if available
        selectedDate = savedInstanceState?.getString("selectedDate") ?: ""

        // If no date is selected, set today's date as the default
        if (selectedDate.isEmpty()) {
            val currentDate = LocalDate.now()
            val dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd", Locale.KOREA)
            selectedDate = currentDate.format(dateFormat)
        }

        // Set the selected date to the text view
        binding.textYearMonth.text = selectedDate

        binding.textYearMonth.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java);
            intent.putExtra("selectedDate", selectedDate)
            //selectedDate= intent.getStringExtra("selectedDate").toString()
            startActivityForResult(intent, CALENDAR_REQUEST_CODE)
        }


        // Check if there is an intent with a date extra
        val intentDate = intent.getStringExtra("intentdate")
        if (intentDate != null) {
            selectedDate = intentDate
            binding.textYearMonth.text = selectedDate
            Log.d("intentdate", intentDate)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("selectedDate", selectedDate)
        super.onSaveInstanceState(outState)
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
//            android.R.id.home -> {
//                onBackPressed()
//                true
//            }
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

        val intentDate = intent.getStringExtra("intentdate")
        if (intentDate != null) {
            selectedDate = intentDate
            binding.textYearMonth.text = selectedDate
            Log.d("intentdate", intentDate)
        }

        //binding.textYearMonth.text = intentDate

        binding.btnCalendarLeft.setOnClickListener {
            binding.textYearMonth.text = null
            binding.textYearMonth.text = intentDate
            /*binding.btnCalendarRight.setOnClickListener{
            binding.textYearMonth.text = null
            binding.textYearMonth.text = LocalDateTime.now().plusDays(1).format(monthFormat).toString()
        }*/
        }
    }

    companion object {
        private const val CALENDAR_REQUEST_CODE = 1
    }
}