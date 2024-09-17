package com.eatssu.android.ui.mypage


import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.BuildConfig
import com.eatssu.android.R
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.repository.FirebaseRemoteConfigRepository
import com.eatssu.android.databinding.ActivityMyPageBinding
import com.eatssu.android.ui.common.VersionViewModel
import com.eatssu.android.ui.common.VersionViewModelFactory
import com.eatssu.android.ui.login.LoginActivity
import com.eatssu.android.ui.mypage.myreview.MyReviewListActivity
import com.eatssu.android.ui.mypage.terms.WebViewActivity
import com.eatssu.android.ui.mypage.usernamechange.UserNameChangeActivity
import com.eatssu.android.util.NotificationReceiver
import com.eatssu.android.util.extension.showToast
import com.eatssu.android.util.extension.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar

@AndroidEntryPoint
class MyPageActivity : BaseActivity<ActivityMyPageBinding>(ActivityMyPageBinding::inflate) {

    private val myPageViewModel: MyPageViewModel by viewModels()

    private lateinit var versionViewModel: VersionViewModel

    private lateinit var firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "마이페이지" // 툴바 제목 설정

        initViewModel()
        setOnClickListener()
        setData()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            } else {
                requestNotificationPermission(this)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        setData() //Todo 최선일까?
    }

    override fun onRestart() {
        super.onRestart()

        setData()
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

        binding.tvSignout.setOnClickListener {
            val intent = Intent(this, SignOutActivity::class.java)
            intent.putExtra("nickname", myPageViewModel.uiState.value.nickname)
            startActivity(intent)
//            showSignoutDialog()
        }

        binding.llDeveloper.setOnClickListener {
            startActivity<DeveloperActivity>()
        }

        binding.llStoreAppVersion.setOnClickListener {
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

        binding.alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (checkNotificationPermission(this)) {
                    scheduleAlarm()
                } else {
                    // 알림 권한이 없을 때 사용자에게 설정 화면으로 이동하라고 알림
                    showNotificationPermissionDialog()
                }
            } else {
                cancelAlarm()
            }
        }
    }

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

    private fun scheduleAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 11)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun cancelAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }


    private fun setData() {
        binding.tvAppVersion.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        binding.tvStoreAppVersion.text = versionViewModel.checkVersionCode().toString()

        myPageViewModel.getMyInfo()

        lifecycleScope.launch {
            Timber.d("관찰시작")
            myPageViewModel.uiState.collectLatest {
                if (it.nickname.isNotEmpty()) {
                    binding.tvNickname.text = it.nickname
                }
            }
        }
    }

    private fun initViewModel() { //Todo 리팩토링하기

        firebaseRemoteConfigRepository = FirebaseRemoteConfigRepository()

        versionViewModel = ViewModelProvider(
            this,
            VersionViewModelFactory(firebaseRemoteConfigRepository)
        )[VersionViewModel::class.java]
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // 권한이 허용되었을 때 알림 설정
                scheduleAlarm()
            } else {
                // 권한이 거부되었을 때 처리
                Toast.makeText(this, "알림 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
                openAppNotificationSettings(this)
            }
        }
    }


    private fun openAppNotificationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        context.startActivity(intent)
    }


    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 1001
    }
}