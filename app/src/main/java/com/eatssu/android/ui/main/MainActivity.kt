package com.eatssu.android.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.eatssu.android.R
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.model.CalendarData
import com.eatssu.android.data.service.MyPageService
import com.eatssu.android.databinding.ActivityMainBinding
import com.eatssu.android.ui.main.calendar.CalendarAdapter
import com.eatssu.android.ui.main.calendar.CalendarAdapter.OnItemListener
import com.eatssu.android.util.CalendarUtils.daysInWeekArray
import com.eatssu.android.util.CalendarUtils.monthYearFromDate
import com.eatssu.android.ui.main.calendar.CalendarViewModel
import com.eatssu.android.ui.mypage.MyPageActivity
import com.eatssu.android.ui.mypage.MypageViewModel
import com.eatssu.android.ui.mypage.MypageViewModelFactory
import com.eatssu.android.ui.mypage.usernamechange.UserNameChangeActivity
import com.eatssu.android.util.RetrofitImpl
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.prolificinteractive.materialcalendarview.*
import java.time.LocalDate
import com.eatssu.android.util.CalendarUtils
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate), OnItemListener {

    private lateinit var viewModel: MypageViewModel

    private var monthYearText: TextView? = null
    private var calendarRecyclerView: RecyclerView? = null

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupNoToolbar()

        initializeMyPageViewModel()
        setupMyPageViewModel()
        observeMyPageViewModel()

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
        val tabTitles = listOf("아침", "점심", "저녁")

        // 2. TabLayout과 ViewPager2를 연결하고, TabItem의 메뉴명을 설정한다.
        TabLayoutMediator(tabLayout, viewPager) { tab, position -> tab.text = tabTitles[position] }.attach()

        binding.btnSetting.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)  // 인텐트를 생성해줌,
            startActivity(intent)  // 화면 전환을 시켜줌
        }

        initWidgets()
        CalendarUtils.selectedDate = LocalDate.now()
        setWeekView()
    }

    private fun initWidgets() {
        calendarRecyclerView = binding.weekRecycler
        monthYearText = binding.monthYearTV
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setWeekView() {
        monthYearText?.setText(CalendarUtils.selectedDate?.let { monthYearFromDate(it) })
        val days: ArrayList<LocalDate>? = CalendarUtils.selectedDate?.let { daysInWeekArray(it) }
        val calendarAdapter = days?.let { CalendarAdapter(it, this) }

        // GridLayoutManager 생성
        val layoutManager: GridLayoutManager = GridLayoutManager(applicationContext, 7, LinearLayoutManager.VERTICAL, false)


        calendarRecyclerView!!.layoutManager = layoutManager
        calendarRecyclerView!!.adapter = calendarAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun previousWeekAction(view: View?) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate!!.minusWeeks(1)
        setWeekView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun nextWeekAction(view: View?) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate!!.plusWeeks(1)
        setWeekView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(position: Int, date: LocalDate?) {
        val viewModel = ViewModelProvider(this@MainActivity)[CalendarViewModel::class.java]

        if (date != null) {
            CalendarUtils.selectedDate = date
            viewModel.setData(date)
            Log.d("maindate", date.toString())
        }

        setWeekView()
    }

    private fun setupNoToolbar() {
        // 툴바 사용하지 않도록 설정
        toolbar.let {
            toolbar.visibility = View.GONE
            toolbarTitle.visibility = View.GONE
            setSupportActionBar(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }

    private fun initializeMyPageViewModel() {
        val myPageService = RetrofitImpl.retrofit.create(MyPageService::class.java)
        viewModel = ViewModelProvider(this, MypageViewModelFactory(myPageService))[MypageViewModel::class.java]
    }

    private fun setupMyPageViewModel(){
        viewModel.checkMyInfo()
    }

    private fun observeMyPageViewModel() {
        viewModel.isNull.observe(this){ it ->
            if(it) {
                Log.d("MainActivity", viewModel.nickname.value.toString())
                val intent = Intent(this, UserNameChangeActivity::class.java)
                startActivity(intent)

            }
        }

        viewModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
}