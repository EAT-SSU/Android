package com.eatssu.android.ui.mypage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
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
import com.eatssu.android.ui.mypage.inquire.InquireActivity
import com.eatssu.android.ui.mypage.myreview.MyReviewListActivity
import com.eatssu.android.ui.mypage.terms.WebViewActivity
import com.eatssu.android.ui.mypage.usernamechange.UserNameChangeActivity
import com.eatssu.android.util.DialogUtil
import com.eatssu.android.util.showToast
import com.eatssu.android.util.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MyPageActivity : BaseActivity<ActivityMyPageBinding>(ActivityMyPageBinding::inflate) {

    private val myPageViewModel: MyPageViewModel by viewModels()

    private lateinit var versionViewModel: VersionViewModel

    private lateinit var firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "마이페이지" // 툴바 제목 설정


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
            startActivity<InquireActivity>()
        }

        binding.llMyReview.setOnClickListener {
            startActivity<MyReviewListActivity>()
        }

        binding.tvLogout.setOnClickListener {
            showLogoutDialog()
        }

        binding.tvSignout.setOnClickListener {
            showSignoutDialog()
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

    }

    private fun setData() {
        binding.tvAppVersion.text = BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")"
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

        DialogUtil.createDialogWithCancelButton(
            "로그아웃",
            this@MyPageActivity,
            "로그아웃 하시겠습니까?",
            "취소",
            "로그아웃"
        ) { _, _ ->
//            ActivityCompat.finishAffinity(this)
//            exitProcess(0)
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
    }


    private fun showSignoutDialog() {
        DialogUtil.createDialogWithCancelButton(
            "탈퇴하기",
            this@MyPageActivity,
            "탈퇴 하시겠습니까?",
            "취소",
            "탈퇴하기"
        ) { _, _ ->
//            ActivityCompat.finishAffinity(this)
//            exitProcess(0)
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