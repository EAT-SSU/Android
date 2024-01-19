package com.eatssu.android.ui.mypage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.App
import com.eatssu.android.BuildConfig
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.repository.FirebaseRemoteConfigRepository
import com.eatssu.android.data.service.MyPageService
import com.eatssu.android.data.service.UserService
import com.eatssu.android.databinding.ActivityMyPageBinding
import com.eatssu.android.ui.common.VersionViewModel
import com.eatssu.android.ui.common.VersionViewModelFactory
import com.eatssu.android.ui.login.SocialLoginActivity
import com.eatssu.android.ui.mypage.Inquire.InquireActivity
import com.eatssu.android.ui.mypage.myreview.MyReviewListActivity
import com.eatssu.android.ui.mypage.usernamechange.UserNameChangeActivity
import com.eatssu.android.util.MySharedPreferences
import com.eatssu.android.util.RetrofitImpl
import com.eatssu.android.util.extension.showToast
import com.eatssu.android.util.extension.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyPageActivity : BaseActivity<ActivityMyPageBinding>(ActivityMyPageBinding::inflate) {
    private lateinit var userService: UserService
    private lateinit var myPageService: MyPageService

    private lateinit var myPageViewModel: MypageViewModel
    private lateinit var versionViewModel: VersionViewModel

    private lateinit var firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "마이페이지" // 툴바 제목 설정


        initializeViewModel()
        setupViewModel()
        observeViewModel()

        bindData()
    }

    override fun onResume() {
        super.onResume()

        setupViewModel()
    }

    private fun bindData(){


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

        binding.tvAppVersion.text = BuildConfig.VERSION_NAME

        binding.tvStoreAppVersion.text = versionViewModel.checkAppVersion()

        binding.llStoreAppVersion.setOnClickListener{
            moveToStore()
        }

    }

    private fun initializeViewModel(){
        userService = RetrofitImpl.retrofit.create(UserService::class.java)
        myPageService = RetrofitImpl.retrofit.create(MyPageService::class.java)

        firebaseRemoteConfigRepository = FirebaseRemoteConfigRepository()

        myPageViewModel = ViewModelProvider(this, MypageViewModelFactory(myPageService))[MypageViewModel::class.java]
        versionViewModel = ViewModelProvider(this, VersionViewModelFactory(firebaseRemoteConfigRepository))[VersionViewModel::class.java]
    }

    private fun setupViewModel() {
        myPageViewModel.checkMyInfo()
    }

    private fun observeViewModel() {
        myPageViewModel.nickname.observe(this) { userNickname ->
            binding.tvNickname.text = userNickname
        }
    }

    private fun showLogoutDialog() {

        // 다이얼로그를 생성하기 위해 Builder 클래스 생성자를 이용해 줍니다.
        val builder = AlertDialog.Builder(this)
        builder.setTitle("로그아웃")
            .setMessage("로그아웃 하시겠습니까?")
            .setPositiveButton("로그아웃"
            ) { _, _ ->
                //로그아웃
                MySharedPreferences.clearUser(this)
                showToast("로그아웃 되었습니다.")
                App.token_prefs.clearTokens() //자동로그인 토큰 날리기
                startActivity<SocialLoginActivity>()
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
                MySharedPreferences.clearUser(this)
                signOut() //탈퇴하기
                showToast("탈퇴 되었습니다.")
                App.token_prefs.clearTokens() //자동로그인 토큰 날리기
                startActivity<SocialLoginActivity>()
            }
            .setNegativeButton("취소") { _, _ ->
            }
        // 다이얼로그를 띄워주기
        builder.show()
    }


    private fun signOut() {
        userService.signOut().enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>,
            ) {
                if (response.isSuccessful){
                    if (response.code() == 200) {
                        Log.d("MyPageActivity", "onResponse 성공: 탈퇴" + response.body().toString())

                    } else {
                        Log.d("MyPageActivity", "onResponse 오류: 탈퇴" + response.body().toString())
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("MyPageActivity", "onFailure 에러: 탈퇴" + t.message.toString())
            }
        })
    }

    private fun moveToStore()
    {
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