package com.eatssu.android.ui.mypage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.BuildConfig
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.repository.FirebaseRemoteConfigRepository
import com.eatssu.android.databinding.ActivityMyPageBinding
import com.eatssu.android.ui.common.VersionViewModel
import com.eatssu.android.ui.common.VersionViewModelFactory
import com.eatssu.android.ui.login.LoginActivity
import com.eatssu.android.ui.mypage.inquire.InquireActivity
import com.eatssu.android.ui.mypage.myreview.MyReviewListActivity
import com.eatssu.android.ui.mypage.usernamechange.UserNameChangeActivity
import com.eatssu.android.util.extension.showToast
import com.eatssu.android.util.extension.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPageActivity : BaseActivity<ActivityMyPageBinding>(ActivityMyPageBinding::inflate) {

    private val myPageViewModel: MyPageViewModel by viewModels()

    private lateinit var versionViewModel: VersionViewModel

    private lateinit var firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "마이페이지" // 툴바 제목 설정


        initViewModel()
        lodeData()
        bindData()
        setOnClickListener()
        setData()
    }

    override fun onResume() {
        super.onResume()

        lodeData()
    }

    override fun onRestart() {
        super.onRestart()

        lodeData()
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
            moveToStore()
        }

    }

    private fun setData() {
        binding.tvAppVersion.text = BuildConfig.VERSION_NAME

        binding.tvStoreAppVersion.text = versionViewModel.checkAppVersion()
    }

    private fun initViewModel() {

        firebaseRemoteConfigRepository = FirebaseRemoteConfigRepository()

        versionViewModel = ViewModelProvider(
            this,
            VersionViewModelFactory(firebaseRemoteConfigRepository)
        )[VersionViewModel::class.java]
    }


    private fun lodeData() {
        myPageViewModel.checkMyInfo()

    }

    private fun bindData() {
        lifecycleScope.launch {
            myPageViewModel.uiState.collectLatest {
                if (!it.error && !it.loading) {
                    binding.tvNickname.text = it.nickname
                }
            }
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
                        }

                    }
                }
            }
            .setNegativeButton("취소") { _, _ ->
            }
        // 다이얼로그를 띄워주기
        builder.show()
    }


    private fun moveToStore() {
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