package com.eatssu.android.presentation.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityMainBinding
import com.eatssu.android.presentation.base.BaseActivity
import com.eatssu.android.presentation.main.calendar.CalendarAdapter
import com.eatssu.android.presentation.main.calendar.CalendarAdapter.OnItemListener
import com.eatssu.android.presentation.main.calendar.CalendarViewModel
import com.eatssu.android.presentation.mypage.MyPageActivity
import com.eatssu.android.presentation.mypage.MyPageViewModel
import com.eatssu.android.presentation.mypage.usernamechange.UserNameChangeActivity
import com.eatssu.android.presentation.util.CalendarUtil
import com.eatssu.android.presentation.util.CalendarUtil.daysInWeekArray
import com.eatssu.android.presentation.util.CalendarUtil.monthYearFromDate
import com.eatssu.android.presentation.util.showToast
import com.eatssu.android.presentation.util.startActivity
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate), OnItemListener {

    private val mainViewModel: MainViewModel by viewModels()
    private val myPageViewModel: MyPageViewModel by viewModels()


    private lateinit var calendarViewModel: CalendarViewModel

    private var monthYearText: TextView? = null
    private var calendarRecyclerView: RecyclerView? = null

    private var mainPosition: Int = -1

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupNoToolbar()
        setUpBottomBar()

        // 알림 퍼미션 있는지 자가 진단
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 권한이 없다면 요청
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1000
                )
            } else {
                // 권한이 이미 있어
            }
        }

        checkNicknameIsNull()

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
            startActivity<MyPageActivity>();
        }

        initWidgets()
        CalendarUtil.selectedDate = LocalDate.now()
        calendarViewModel.setData(LocalDate.now())
        setWeekView()
    }

    private fun initWidgets() {
        calendarRecyclerView = binding.weekRecycler
        monthYearText = binding.monthYearTV
        calendarViewModel = ViewModelProvider(this)[CalendarViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setWeekView() {
        monthYearText?.setText(CalendarUtil.selectedDate?.let { monthYearFromDate(it) })
        val days: ArrayList<LocalDate>? = CalendarUtil.selectedDate?.let { daysInWeekArray(it) }
        val calendarAdapter = days?.let { CalendarAdapter(it, this) }
        val gridLayoutManager = GridLayoutManager(applicationContext, 7)

        calendarRecyclerView!!.layoutManager = gridLayoutManager
        calendarRecyclerView!!.adapter = calendarAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun previousWeekAction(view: View?) {
        CalendarUtil.selectedDate = CalendarUtil.selectedDate.minusWeeks(1)
        onItemClick(mainPosition, CalendarUtil.selectedDate)
        setWeekView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun nextWeekAction(view: View?) {
        CalendarUtil.selectedDate = CalendarUtil.selectedDate.plusWeeks(1)
        onItemClick(mainPosition, CalendarUtil.selectedDate)
        setWeekView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(position: Int, date: LocalDate) {
        CalendarUtil.selectedDate = date
        calendarViewModel.setData(date)
        mainPosition = position

        val viewPager: ViewPager2 = binding.vpMain
        val tabLayout: TabLayout = binding.tabLayout

        val viewpagerFragmentAdapter = ViewPager2Adapter(this)

        viewPager.adapter = viewpagerFragmentAdapter
        viewPager.setCurrentItem(viewpagerFragmentAdapter.getDefaultFragmentPosition(), false)

        val tabTitles = listOf("아침", "점심", "저녁")

        TabLayoutMediator(tabLayout, viewPager) { tab, position -> tab.text = tabTitles[position] }.attach()

        binding.btnSetting.setOnClickListener {
            startActivity<MyPageActivity>()
        }

        initWidgets()
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


    private fun checkNicknameIsNull() {
        Timber.d("관찰 시작")
        mainViewModel.checkNameNull()

        lifecycleScope.launch {
            mainViewModel.uiState.collectLatest {
                if (it.isNicknameNull) {
                    //닉네임이 null일 때는 닉네임 설정을 안하면 서비스를 못쓰게 막아야함
                    intent.putExtra("force", true)
                    startActivity<UserNameChangeActivity>()
                    showToast(it.toastMessage)
                } else {
                    showToast(it.toastMessage) //Todo 이게 누구님 반갑습니다. 인데 두번 뜸
                }
            }
        }
    }

    private fun setUpBottomBar() {
        binding.bottomNaviBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cafeteria_menu -> {
                    Log.d("NaviTest", "Cafeteria Menu Clicked")
                    true
                }
                R.id.mypage -> {
                    Log.d("NaviTest", "My Page Clicked")
                    // startActivity<MyPageActivity>()
                    true
                }
                else -> false
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

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        if (requestCode == 1000) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 승인됨
                showToast("EAT-SSU 알림 수신을 동의하였습니다.")
                myPageViewModel.setNotificationOn() //바로 알림 받도록 설정
            } else {
                // 권한이 거부됨
                showToast("EAT-SSU 알림 수신을 거부하였습니다.\n$dateFormat")
                myPageViewModel.setNotificationOff() //바로 알림 받도록 설정
            }
        }
    }
}