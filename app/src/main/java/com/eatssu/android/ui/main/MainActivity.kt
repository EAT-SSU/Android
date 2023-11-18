package com.eatssu.android.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.eatssu.android.R
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.util.RetrofitImpl
import com.eatssu.android.data.entity.CalendarData
import com.eatssu.android.data.model.response.GetMyInfoResponseDto
import com.eatssu.android.data.service.MyPageService
import com.eatssu.android.databinding.ActivityMainBinding
import com.eatssu.android.data.repository.FirebaseRemoteConfigRepository
import com.eatssu.android.ui.common.ForceUpdateDialogActivity
import com.eatssu.android.ui.common.VersionViewModel
import com.eatssu.android.ui.common.VersionViewModelFactory
import com.eatssu.android.ui.info.InfoViewModel
import com.eatssu.android.ui.main.calendar.CalendarAdapter
import com.eatssu.android.ui.main.calendar.OnItemClickListener
import com.eatssu.android.ui.mypage.usernamechange.UserNameChangeActivity
import com.eatssu.android.ui.mypage.MyPageActivity
import com.eatssu.android.ui.main.calendar.CalendarViewModel
import com.eatssu.android.ui.info.InfoViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.prolificinteractive.materialcalendarview.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    lateinit var calendarAdapter: CalendarAdapter
    private var calendarList = ArrayList<CalendarData>()

    private lateinit var versionViewModel: VersionViewModel
    private lateinit var firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository

    private lateinit var nickname: String

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseRemoteConfigRepository = FirebaseRemoteConfigRepository()
        versionViewModel = ViewModelProvider(this, VersionViewModelFactory(firebaseRemoteConfigRepository))[VersionViewModel::class.java]

        if(versionViewModel.checkForceUpdate()){
            showForceUpdateDialog()
        }

        // 툴바 사용하지 않도록 설정
        toolbar.let {
            toolbar.visibility= View.GONE
            toolbarTitle.visibility= View.GONE
            setSupportActionBar(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        val intentNick = Intent(this, UserNameChangeActivity::class.java)

        val myPageService =
            RetrofitImpl.retrofit.create(MyPageService::class.java)

        myPageService.getMyInfo().enqueue(object :
            Callback<GetMyInfoResponseDto> {
            override fun onResponse(
                call: Call<GetMyInfoResponseDto>,
                response: Response<GetMyInfoResponseDto>
            ) {
                if (response.isSuccessful) {
                    Log.d("MainActivity", "onResponse 성공: " + response.body().toString())
                    nickname = response.body()?.nickname.toString()
                    Log.d("MainActivity", "onResponse 성공: $nickname")

                    //나중에 isNullOrBlank로 바꿀 것
                    if (nickname == "null") {
                        startActivity(intentNick)
                        Log.d("MainActivity","닉네임이 null")
                    }
                }
            }

            override fun onFailure(call: Call<GetMyInfoResponseDto>, t: Throwable) {
                Log.d("MainActivity", "onFailure 에러: " + t.message.toString())
            }
        })

//        supportActionBar?.title = "EAT-SSU"

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

        binding.btnSetting.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)  // 인텐트를 생성해줌,
            startActivity(intent)  // 화면 전환을 시켜줌
        }

        var week_day: Array<String> = resources.getStringArray(R.array.calendar_day)

        calendarAdapter = CalendarAdapter(calendarList)

        val viewModel = ViewModelProvider(this@MainActivity)[CalendarViewModel::class.java]

        calendarList.apply {
            val dateFormat =
                DateTimeFormatter.ofPattern("dd").withLocale(Locale.forLanguageTag("ko"))
            val monthFormat = DateTimeFormatter.ofPattern("yyyy . MM . dd")
                .withLocale(Locale.forLanguageTag("ko"))
            val dayFormat = DateTimeFormatter.ofPattern("dd")

            val todayDate = LocalDateTime.now().format(dayFormat)
            viewModel.setData(todayDate)

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

                //val viewModel = ViewModelProvider(this@MainActivity)[CalendarViewModel::class.java]
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


    private fun showForceUpdateDialog() {
        val intent = Intent(this, ForceUpdateDialogActivity::class.java)
        startActivity(intent)
    }

}