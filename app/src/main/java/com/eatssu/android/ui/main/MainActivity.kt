package com.eatssu.android.ui.main

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.eatssu.android.R
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.eatssu.android.databinding.ActivityMainBinding
import com.eatssu.android.ui.BaseActivity
import com.eatssu.android.ui.calendar.CalendarFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.prolificinteractive.materialcalendarview.*
import java.util.*

class MainActivity : BaseActivity() {
    private lateinit var viewBinding : ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        //setContentView(viewBinding.root)
        val inflater = LayoutInflater.from(this)
        inflater.inflate(R.layout.activity_main, findViewById(R.id.frame_layout), true)
        findViewById<FrameLayout>(R.id.frame_layout).addView(viewBinding.root)

        supportActionBar?.title = "비밀번호 변경"

        /*supportFragmentManager
            .beginTransaction()
            .replace(viewBinding.containerFragment.id,CalendarFragment())
            .commitAllowingStateLoss()*/

        supportFragmentManager
            .beginTransaction()
            .add(viewBinding.frame.id, CalendarFragment())
            .commitAllowingStateLoss()

        // 1) ViewPager2 참조
        val viewPager: ViewPager2 = viewBinding.vpMain
        val tabLayout: TabLayout = viewBinding.tabLayout

        // 2) FragmentStateAdapter 생성 : Fragment 여러개를 ViewPager2에 연결해주는 역할
        val viewpagerFragmentAdapter = ViewPager2Adapter(this)

        // 3) ViewPager2의 adapter에 설정
        viewPager.adapter = viewpagerFragmentAdapter


        // ###### TabLayout과 ViewPager2를 연결
        // 1. 탭메뉴의 이름을 리스트로 생성해둔다.
        val tabTitles = listOf<String>("아침", "점심", "저녁")

        // 2. TabLayout과 ViewPager2를 연결하고, TabItem의 메뉴명을 설정한다.
        TabLayoutMediator(tabLayout, viewPager, {tab, position -> tab.text = tabTitles[position]}).attach()





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

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_main
    }
}


