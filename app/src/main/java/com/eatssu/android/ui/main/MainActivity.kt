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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        //setContentView(viewBinding.root)
//
        /*val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.activity_main, findViewById(R.id.frame_layout), true)
        findViewById<FrameLayout>(R.id.frame_layout).addView(binding.root)*/

        setContentView(binding.root)
        supportActionBar?.title = "EAT-SSU"

        /*supportFragmentManager
            .beginTransaction()
            .replace(viewBinding.containerFragment.id,CalendarFragment())
            .commitAllowingStateLoss()*/

        /*upportFragmentManager
            .beginTransaction()
            .add(binding.frame.id, CalendarFragment())
            .commitAllowingStateLoss()*/

        // 1) ViewPager2 참조
        val viewPager: ViewPager2 = binding.vpMain
        val tabLayout: TabLayout = binding.tabLayout

        // 2) FragmentStateAdapter 생성 : Fragment 여러개를 ViewPager2에 연결해주는 역할
        val viewpagerFragmentAdapter = ViewPager2Adapter(this)

        // 3) ViewPager2의 adapter에 설정
        viewPager.adapter = viewpagerFragmentAdapter


        // ###### TabLayout과 ViewPager2를 연결
        // 1. 탭메뉴의 이름을 리스트로 생성해둔다.
        val tabTitles = listOf<String>("아침", "점심", "저녁")

        // 2. TabLayout과 ViewPager2를 연결하고, TabItem의 메뉴명을 설정한다.
        TabLayoutMediator(tabLayout, viewPager, {tab, position -> tab.text = tabTitles[position]}).attach()

        binding.btnSetting.setOnClickListener() {
            val intent = Intent(this, MyPageActivity::class.java)  // 인텐트를 생성해줌,
            startActivity(intent)  // 화면 전환을 시켜줌
        }
        val monthFormat = DateTimeFormatter.ofPattern("yyyy . MM . dd")
            .withLocale(Locale.forLanguageTag("ko"))
        val localDate = LocalDateTime.now().format(monthFormat)
        binding.textYearMonth.text = localDate


        binding.textYearMonth.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java);
            startActivity(intent);
            onStop()

        }

        //text 키값으로 데이터를 받는다. String을 받아야 하므로 getStringExtra()를 사용함
        /*val selectdate = this.arguments?.getString("changedate")
        Log.d("selectdate", selectdate.toString())*/




        //Log.d("getKeyHash", "" + getKeyHash(this));

        /*@SuppressLint("PackageManagerGetSignatures")
    open fun getKeyHash(context: Context): String? {
        val pm: PackageManager = context.getPackageManager()
        try {
            val packageInfo: PackageInfo =
                pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES)
                    ?: return null
            for (signature in packageInfo.signatures) {
                try {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    return encodeToString(md.digest(), NO_WRAP)
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }*/
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
            R.id.action_setting ->{
                val intent = Intent(this, MyPageActivity::class.java)  // 인텐트를 생성해줌,
                startActivity(intent)  // 화면 전환을 시켜줌
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
//
//    override fun getLayoutResourceId(): Int {
//        return R.layout.activity_main
//    }

    override fun onRestart() {

        val intentdate = intent.getStringExtra("intentdate")
        if (intentdate != null) {
            Log.d("intentdate", intentdate)
        }

        binding.textYearMonth.text = intentdate

        binding.btnCalendarLeft.setOnClickListener {
            binding.textYearMonth.text = null
            binding.textYearMonth.text = intentdate
            /*binding.btnCalendarRight.setOnClickListener{
            binding.textYearMonth.text = null
            binding.textYearMonth.text = LocalDateTime.now().plusDays(1).format(monthFormat).toString()
        }*/
        }
        super.onRestart()
    }
}

