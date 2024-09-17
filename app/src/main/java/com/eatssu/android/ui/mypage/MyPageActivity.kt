package com.eatssu.android.ui.mypage

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
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
import com.eatssu.android.ui.main.MainActivity
import com.eatssu.android.ui.mypage.myreview.MyReviewListActivity
import com.eatssu.android.ui.mypage.terms.WebViewActivity
import com.eatssu.android.ui.mypage.usernamechange.UserNameChangeActivity
import com.eatssu.android.util.extension.showToast
import com.eatssu.android.util.extension.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MyPageActivity : BaseActivity<ActivityMyPageBinding>(ActivityMyPageBinding::inflate) {

    private val myPageViewModel: MyPageViewModel by viewModels()

    private lateinit var versionViewModel: VersionViewModel

    private lateinit var firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository

//    private lateinit var alarmSwitch: SwitchCompat


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "마이페이지" // 툴바 제목 설정

//        alarmSwitch.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
//            if (isChecked) {//true
//                myPageViewModel.setNotification()
//            } else {//false
//                myPageViewModel.cancelNotification()
//            }
//        }

        initViewModel()
        setOnClickListener()
        setData()
    }

    override fun onResume() {
        super.onResume()

        setData() //Todo 최선일까?
    }

    override fun onRestart() {
        super.onRestart()

        setData()
    }

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

        binding.btnNotify.setOnClickListener {
//            myPageViewModel.startNotify()

            val notificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "test_notification_channel"

            // NotificationChannel 설정 (API 26 이상)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Test Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }

            // MainActivity로 이동하는 인텐트 설정
            val mainIntent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // 알림 생성
            val notification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.img_logo2)
                .setContentTitle(getString(R.string.notification_context_title))
                .setContentText(getString(R.string.notification_context_text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            // 알림 발송
            notificationManager.notify(1, notification)
        }
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
}