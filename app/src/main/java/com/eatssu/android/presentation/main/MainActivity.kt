package com.eatssu.android.presentation.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityMainBinding
import com.eatssu.android.presentation.base.BaseActivity
import com.eatssu.android.presentation.login.LoginActivity
import com.eatssu.android.presentation.mypage.MyPageViewModel
import com.eatssu.android.presentation.mypage.usernamechange.UserNameChangeActivity
import com.eatssu.android.presentation.util.showToast
import com.eatssu.android.presentation.util.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate){

    private val mainViewModel: MainViewModel by viewModels()
    private val myPageViewModel: MyPageViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupNoToolbar()
        setUpBottomBar()

        checkAlarmPermission()

        checkNicknameIsNull()
        collectLogoutState()

    }

    // set UI --
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

    private fun setUpBottomBar() {
        // ViewPager2 + Adapter 연결
        val viewPagerAdapter = MainViewPager2Adapter(this)
        binding.vpMain.adapter = viewPagerAdapter
        binding.vpMain.isUserInputEnabled = false // 스와이프 막기

        // BottomNavigation 클릭 시 ViewPager 이동
        binding.bottomNaviBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.cafeteria_menu -> binding.vpMain.setCurrentItem(0, false)
                R.id.mypage_menu -> binding.vpMain.setCurrentItem(1, false)
            }
            true
        }

        // ViewPager 이동 시 BottomNavigation 체크 변경
        binding.vpMain.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val itemId = when (position) {
                    0 -> R.id.cafeteria_menu
                    1 -> R.id.mypage_menu
                    else -> R.id.cafeteria_menu
                }
                binding.bottomNaviBar.selectedItemId = itemId
            }
        })

//        binding.bottomNaviBar.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.cafeteria_menu -> {
//                    Log.d("NaviTest", "Cafeteria Menu Clicked")
//                    true
//                }
//                R.id.mypage -> {
//                    Log.d("NaviTest", "My Page Clicked")
//                    // startActivity<MyPageActivity>()
//                    true
//                }
//                else -> false
//            }
//        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_setting -> {
//                val intent = Intent(this, MyPageActivity::class.java)  // 인텐트를 생성해줌,
//                startActivity(intent)  // 화면 전환을 시켜줌
//                true
//            }
//
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    // Permission --
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

    // 알림 퍼미션 있는지 자가 진단
    private fun checkAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 권한이 없다면 요청
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1000
                )
            } else {
                // 권한이 이미 있어
            }
        }
    }

    // CollectState --

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

    // 로그아웃 처리
    private fun collectLogoutState() {
        lifecycleScope.launch {
            mainViewModel.uiState.collectLatest { state ->
                if (state.isLoggedOut) {
                    showToast(state.toastMessage)
                    startActivity<LoginActivity>()
                    finishAffinity()
                }
            }
        }
    }
}