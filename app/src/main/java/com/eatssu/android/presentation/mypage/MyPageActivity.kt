package com.eatssu.android.presentation.mypage


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityMyPageBinding
import com.eatssu.android.presentation.base.BaseActivity
import com.eatssu.android.presentation.login.LoginActivity
import com.eatssu.android.presentation.mypage.myreview.MyReviewListActivity
import com.eatssu.android.presentation.mypage.terms.WebViewActivity
import com.eatssu.android.presentation.mypage.usernamechange.UserNameChangeActivity
import com.eatssu.android.presentation.util.showToast
import com.eatssu.android.presentation.util.startActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class MyPageActivity : BaseActivity<ActivityMyPageBinding>(ActivityMyPageBinding::inflate) {

    private val myPageViewModel: MyPageViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "마이페이지" // 툴바 제목 설정

        binding.tvSignout.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        setupObservers()
        setOnClickListener()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                myPageViewModel.uiState.collect {
                    binding.tvAppVersion.text = it.appVersion

                    if (it.nickname.isNotEmpty()) {
                        binding.tvNickname.text = it.nickname
                    }

                    // Switch 상태를 설정할 때 리스너를 임시로 null로 설정
                    binding.alarmSwitch.setOnCheckedChangeListener(null)
                    binding.alarmSwitch.isChecked = it.isAlarmOn
                    // 상태 설정 후에 리스너 추가
                    binding.alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
                        handleAlarmSwitchChange(isChecked)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleAlarmSwitchChange(isChecked: Boolean) {
        val nowDatetime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val formattedDate = nowDatetime.format(formatter)

        if (isChecked) {
            if (checkNotificationPermission(this)) { // 권한이 있는 상태
                myPageViewModel.setNotificationOn()
                showSnackbar("EAT-SSU 알림 수신을 동의하였습니다.\n$formattedDate")
            } else { // 권한이 없으면 설정 화면으로 이동 알림
                showNotificationPermissionDialog()
            }
        } else {
            myPageViewModel.setNotificationOff()
            showSnackbar("EAT-SSU 알림 수신을 거부하였습니다.\n$formattedDate")
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setOnClickListener() {
        binding.llNickname.setOnClickListener {
            startActivity<UserNameChangeActivity>()
        }

        binding.llInquire.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra("URL", getString(R.string.kakao_talk_channel_url))
            intent.putExtra("TITLE", getString(R.string.contact))
            startActivity(intent)
        }

        binding.llMyReview.setOnClickListener {
            startActivity<MyReviewListActivity>()
        }

        binding.tvLogout.setOnClickListener {
            showLogoutDialog()
        }

        binding.llSignout.setOnClickListener {
            val intent = Intent(this, SignOutActivity::class.java)
            intent.putExtra("nickname", myPageViewModel.uiState.value.nickname)
            startActivity(intent)
        }

        binding.llDeveloper.setOnClickListener {
            startActivity<DeveloperActivity>()
        }

        binding.llAppVersion.setOnClickListener {
            moveToPlayStore()
        }

        binding.llServiceRule.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra("URL", getString(R.string.terms_url))
            intent.putExtra("TITLE", getString(R.string.terms))
            startActivity(intent)
        }

        binding.llPrivateInformation.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra("URL", getString(R.string.policy_url))
            intent.putExtra("TITLE", getString(R.string.policy))
            startActivity(intent)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotificationPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("알림 권한 필요")
            .setMessage("알림을 받으려면 알림 권한을 활성화해야 합니다. 설정 화면으로 이동하시겠습니까?")
            .setPositiveButton("설정으로 이동") { _, _ ->
                openAppNotificationSettings(this)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun checkNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Android 13 이전 버전에서는 알림 권한이 필요하지 않음
        }
    }

    private fun showLogoutDialog() {

        // 다이얼로그를 생성하기 위해 Builder 클래스 생성자를 이용해 줍니다.
        val builder = AlertDialog.Builder(this)
        builder.setTitle("로그아웃")
            .setMessage("로그아웃 하시겠습니까?")
            .setPositiveButton(
                "로그아웃"
            ) { _, _ ->
                //로그아웃
                myPageViewModel.loginOut()

                lifecycleScope.launch {
                    myPageViewModel.uiState.collectLatest {
                        if (it.isLoginOuted) {
                            showToast(it.toastMessage)
                            startActivity<LoginActivity>()
                            finishAffinity()
                        }

                    }
                }
            }
            .setNegativeButton("취소") { _, _ ->
            }
        // 다이얼로그를 띄워주기
        builder.show()
    }

    private fun showSignoutDialog() {

        // 다이얼로그를 생성하기 위해 Builder 클래스 생성자를 이용해 줍니다.
        val builder = AlertDialog.Builder(this)
        builder.setTitle("탈퇴하기")
            .setMessage("탈퇴 하시겠습니까?")
            .setPositiveButton(
                "탈퇴하기"
            ) { _, _ ->
                //탈퇴처리
                myPageViewModel.signOut()

                lifecycleScope.launch {
                    myPageViewModel.uiState.collectLatest {
                        if (it.isSignOuted) {
                            showToast(it.toastMessage)
                            startActivity<LoginActivity>()
                            finishAffinity()
                        }

                    }
                }
            }
            .setNegativeButton("취소") { _, _ ->
            }
        // 다이얼로그를 띄워주기
        builder.show()
    }


    private fun moveToPlayStore() {
        val appPackageName = packageName
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (e: android.content.ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    // 알림 권한 요청 함수
    private fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATION_PERMISSION
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // 권한이 허용되었을 때 알림 설정
                myPageViewModel.setNotificationOn()
            } else {
                // 권한이 거부되었을 때 처리
                showToast("EAT-SSU 알림 수신을 거부하였습니다.")
                openAppNotificationSettings(this)
                myPageViewModel.setNotificationOff()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun openAppNotificationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        context.startActivity(intent)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 1001
    }
}