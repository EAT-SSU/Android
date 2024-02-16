package com.eatssu.android.ui.login

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.App
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.data.service.OauthService
import com.eatssu.android.databinding.ActivitySocialLoginBinding
import com.eatssu.android.ui.main.MainActivity
import com.eatssu.android.util.MySharedPreferences
import com.eatssu.android.util.RetrofitImpl
import com.eatssu.android.util.extension.showToast
import com.eatssu.android.util.extension.startActivity
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch


class LoginActivity : BaseActivity<ActivitySocialLoginBinding>(ActivitySocialLoginBinding::inflate) {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var oauthService: OauthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        oauthService = RetrofitImpl.nonRetrofit.create(OauthService::class.java)
        loginViewModel =
            ViewModelProvider(this, LoginViewModelFactory(oauthService))[LoginViewModel::class.java]
//        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]


        // 툴바 사용하지 않도록 설정
        toolbar.let {
            toolbar.visibility = View.GONE
            toolbarTitle.visibility = View.GONE
            setSupportActionBar(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }


        checkAutoLogin()
        bind()
    }

    private fun checkAutoLogin() {
        // SharedPreferences 안에 값이 저장되어 있을 때-> Login 패스하기
        if (MySharedPreferences.getUserEmail(this).isNotBlank())
        {
            // SharedPreferences 안에 값이 저장되어 있을 때 -> MainActivity로 이동
            Toast.makeText(this, "자동 로그인 되었습니다.", Toast.LENGTH_SHORT).show()
            startActivity<MainActivity>()
            finish()
        }
    }

    private fun bind(){
        val context = this
        binding.mcvKakaoLogin.setOnClickListener {

            Log.d("post", "버튼 클릭")
            lifecycleScope.launch {
                try {
                    // 서비스 코드에서는 간단하게 로그인 요청하고 oAuthToken 을 받아올 수 있다.
                    val oAuthToken = UserApiClient.loginWithKakao(context)
                    Log.d("MainActivity", "beanbean > $oAuthToken")
                    postUserInfo()

                } catch (error: Throwable) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        Log.d("MainActivity", "사용자가 명시적으로 취소")
                    } else {
                        Log.e("MainActivity", "인증 에러 발생", error)
                    }
                }
            }
        }
    }


    private fun postUserInfo() {
        UserApiClient.instance.me { user, error ->
            if (user != null) {
                // 유저의 아이디
                Log.d(TAG, "invoke: id =" + user.id)
                val providerID = user.id.toString()
                // 유저의 이메일
                Log.d(TAG, "invoke: email =" + user.kakaoAccount!!.email)
                val email = user.kakaoAccount!!.email.toString()

                loginViewModel.getLogin(email, providerID)
                loginViewModel.getHomeLoginSuccessResponse.observe(this) { successData ->
                    successData.run {
                        /*자동 로그인*/
                        MySharedPreferences.setUserEmail(this@LoginActivity, email)
                        MySharedPreferences.setUserPlatform(this@LoginActivity, "KAKAO")

                        /*토큰 저장*/
                        App.token_prefs.accessToken = successData?.accessToken
                        App.token_prefs.refreshToken = successData?.refreshToken

                        Toast.makeText(this@LoginActivity, "$email 계정으로 로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                        startActivity<MainActivity>()  // 화면 전환을 시켜줌
                    }
                }

                loginViewModel.getHomeLoginErrorResponse.observe(this) { errorMessage ->
                    showToast(errorMessage)
                    Log.d("errorMessage",errorMessage)
                }
            }
        }
    }
}