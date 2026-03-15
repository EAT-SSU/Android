package com.eatssu.android.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.MenuItem
import android.view.View.GONE
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.work.WorkManager
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityMainBinding
import com.eatssu.android.presentation.base.BaseActivity
import com.eatssu.android.presentation.event.AnyoneButMeEventPopupController
import com.eatssu.android.presentation.login.LoginActivity
import com.eatssu.android.presentation.mypage.MyPageViewModel
import com.eatssu.android.presentation.mypage.userinfo.UserInfoActivity
import com.eatssu.android.presentation.util.showInfoToast
import com.eatssu.android.presentation.util.showToast
import com.eatssu.android.presentation.util.startActivity
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.enums.ScreenId
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(
    ActivityMainBinding::inflate,
    ScreenId.HOME_MAIN
) {

    @Inject
    lateinit var workManager: WorkManager

    @Inject
    lateinit var anyoneButMeEventPopupController: AnyoneButMeEventPopupController

    private val mainViewModel: MainViewModel by viewModels()
    private val myPageViewModel: MyPageViewModel by viewModels()

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupNoToolbar()
        setNavigation()
        bindEventPopup(showOnLaunch = savedInstanceState == null)

        checkAlarmPermission()
        collectState()
        collectUiEvents()
    }

    private fun setNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNaviBar.itemIconTintList = null

        binding.bottomNaviBar.setOnSingleItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cafeteria_menu -> {
                    navController.navigate(R.id.cafeteria_menu)
                    true
                }

                R.id.map_menu -> {
                    navController.navigate(R.id.mapFragment)
                    true
                }

                R.id.anyone_but_me_menu -> {
                    anyoneButMeEventPopupController.openAnyoneButMePage()
                    false
                }

                R.id.mypage_menu -> {
                    navController.navigate(R.id.myPageFragment)
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun bindEventPopup(showOnLaunch: Boolean) {
        anyoneButMeEventPopupController.bind(
            composeView = binding.composeEventPopup,
            lifecycleScope = lifecycleScope,
            showOnLaunch = showOnLaunch
        )
    }

    // set UI --
    private fun setupNoToolbar() {
        // 툴바 사용하지 않도록 설정
        toolbar.let {
            toolbar.visibility = GONE
            toolbarTitle.visibility = GONE
            setSupportActionBar(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }

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
                showInfoToast("EAT-SSU 알림 수신을 동의하였습니다.")
                myPageViewModel.setNotificationOn() //바로 알림 받도록 설정
            } else {
                // 권한이 거부됨
                showInfoToast("EAT-SSU 알림 수신을 거부하였습니다.\n$dateFormat")
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
    private fun collectState() {
        lifecycleScope.launch {
            mainViewModel.uiState.collectLatest { state ->
                if (state is UiState.Success) {
                    when (state.data) {
                        is MainState.NicknameNull -> {
                            intent.putExtra("force", true)
                            startActivity<UserInfoActivity>()
                        }

                        is MainState.LoggedOut -> {
                            startActivity<LoginActivity>()
                            finishAffinity()
                        }

                        else -> Unit
                    }
                } else Unit
            }
        }
    }

    // UiEvent 처리
    private fun collectUiEvents() {
        lifecycleScope.launch {
            mainViewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is UiEvent.ShowToast -> showToast(event)
                }
            }
        }
    }

    // 아래 함수를 View 에서는 사용이 어려워 util 로 빼지 않음
    private fun BottomNavigationView.setOnSingleItemSelectedListener(
        minInterval: Long = 500L,
        onSingleItemSelected: (item: MenuItem) -> Boolean
    ) {
        var lastClickTime = 0L

        setOnItemSelectedListener { item ->
            val currentClickTime = SystemClock.uptimeMillis()
            if (currentClickTime - lastClickTime > minInterval) {
                lastClickTime = currentClickTime
                onSingleItemSelected(item)
            } else {
                false // 너무 빠른 클릭 무시
            }
        }
    }

    override fun shouldLogScreenId() = false
}
