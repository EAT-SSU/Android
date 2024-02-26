package com.eatssu.android.ui.login

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity :
    BaseActivity<ActivitySocialLoginBinding>(ActivitySocialLoginBinding::inflate) {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var oauthService: OauthService


//    private val loginViewModel: LoginViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        oauthService = RetrofitImpl.nonRetrofit.create(OauthService::class.java)
        loginViewModel =
            ViewModelProvider(this, LoginViewModelFactory(oauthService))[LoginViewModel::class.java]


//        MainScope().launch {  loginViewModel }

        // 툴바 사용하지 않도록 설정
        toolbar.let {
            toolbar.visibility = View.GONE
            toolbarTitle.visibility = View.GONE
            setSupportActionBar(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }


        // SharedPreferences 안에 값이 저장되어 있을 때-> Login 패스하기
        if (MySharedPreferences.getUserEmail(this).isNotBlank()) {
            // SharedPreferences 안에 값이 저장되어 있을 때 -> MainActivity로 이동
            Toast.makeText(
                this,
                "자동 로그인 되었습니다.",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val context = this


        binding.mcvKakaoLogin.setOnClickListener {

            Log.d("LoginActivity", "버튼 클릭")
            lifecycleScope.launch {
                try {
                    // 서비스 코드에서는 간단하게 로그인 요청하고 oAuthToken 을 받아올 수 있다.
                    val oAuthToken = UserApiClient.loginWithKakao(context)
                    Log.d("LoginActivity", "beanbean > $oAuthToken")
                    postUserInfo()

                } catch (error: Throwable) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        Log.d("LoginActivity", "사용자가 명시적으로 취소")
                    } else {
                        Log.e("LoginActivity", "인증 에러 발생", error)
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

                lifecycleScope.launch {
                    loginViewModel.state.collectLatest {
                        if (!it.error && !it.loading) {
                            Log.d("login", it.toString())
                            showToast(loginViewModel.state.value.toastMessage.toString())
                            startActivity<MainActivity>()
                        }
                    }
                }
            }
        }
    }
}