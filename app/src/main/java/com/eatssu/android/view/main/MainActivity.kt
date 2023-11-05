package com.eatssu.android.view.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.eatssu.android.R
import com.eatssu.android.adapter.CalendarAdapter
import com.eatssu.android.adapter.OnItemClickListener
import com.eatssu.android.data.NetworkConnection
import com.eatssu.android.data.RetrofitImpl
import com.eatssu.android.data.entity.CalendarData
import com.eatssu.android.data.model.response.GetMyInfoResponseDto
import com.eatssu.android.data.service.MyPageService
import com.eatssu.android.databinding.ActivityMainBinding
import com.eatssu.android.view.mypage.ChangeNicknameActivity
import com.eatssu.android.view.mypage.MyPageActivity
import com.eatssu.android.viewmodel.CalendarViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.prolificinteractive.materialcalendarview.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var calendarAdapter: CalendarAdapter
    private var calendarList = ArrayList<CalendarData>()

    private val networkCheck: NetworkConnection by lazy {
        NetworkConnection(this)
    }

    private val firebaseRemoteConfig: FirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance()
    }

    private lateinit var nickname: String

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Firebase Remote Config 초기화 설정
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600) // 캐시된 값을 1시간마다 업데이트
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)

        // 기본값 설정 (강제 업데이트 여부와 버전 정보)
        val defaultValues: Map<String, Any> = mapOf(
            "force_update_required" to false,
            "latest_app_version" to "1.0.0"
        )
        firebaseRemoteConfig.setDefaultsAsync(defaultValues)

        // Firebase Remote Config 데이터 가져오기
        fetchRemoteConfig()

        // 메인 액티비티에서 Firebase Remote Config를 사용하여 업데이트 필요 여부 확인
        checkForUpdate()

        networkCheck.register() // 네트워크 객체 등록

        val intentNick = Intent(this, ChangeNicknameActivity::class.java)

        val myPageService =
            RetrofitImpl.retrofit.create(MyPageService::class.java)
            myPageService.getMyInfo().enqueue(object :
            Callback<GetMyInfoResponseDto> {
            override fun onResponse(
                call: Call<GetMyInfoResponseDto>,
                response: Response<GetMyInfoResponseDto>
            ) {
                if (response.isSuccessful) {
                    Log.d("post", "onResponse 성공: " + response.body().toString());
                    nickname = response.body()?.nickname.toString()
                    //나중에 isNullOrBlank로 바꿀 것
                    if (nickname == null) {
                        startActivity(intentNick)
                    }
                }
            }

            override fun onFailure(call: Call<GetMyInfoResponseDto>, t: Throwable) {
                Log.d("post", "onFailure 에러: " + t.message.toString());
            }
        })

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

    private fun fetchRemoteConfig() {
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Remote Config 데이터 가져오기 성공
                    // 필요한 경우 업데이트 필요 여부 확인을 수행
                    checkForUpdate()
                }
            }
    }

    private fun checkForUpdate() {
        val forceUpdateRequired = firebaseRemoteConfig.getBoolean("force_update")
        val latestAppVersion = firebaseRemoteConfig.getString("app_version")

        val currentAppVersion = BuildConfig.VERSION_NAME

        if (forceUpdateRequired) {
            Log.d("post", forceUpdateRequired.toString())
            Log.d("post", latestAppVersion)
            Log.d("post", currentAppVersion)
            // 강제 업데이트 다이얼로그를 띄우거나 업데이트 화면으로 이동
            showForceUpdateDialog()
        }
    }

    private fun showForceUpdateDialog() {
        val intent = Intent(this, ForceUpdateDialogActivity::class.java)
        startActivity(intent)
    }

        override fun onDestroy() {
            super.onDestroy()

            networkCheck.unregister() // 네트워크 객체 해제
        }
    }