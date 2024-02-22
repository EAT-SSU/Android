package com.eatssu.android.ui.login

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivitySocialLoginBinding>(ActivitySocialLoginBinding::inflate) {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // 툴바 사용하지 않도록 설정
        toolbar.let {
            toolbar.visibility = View.GONE
            toolbarTitle.visibility = View.GONE
            setSupportActionBar(it)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

//        binding.viewModel = viewModel
//        binding.lifecycleOwner = viewLifecycleOwner
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


                lifecycleScope.launch {
                    viewModel.getLogin(email, providerID)

                    viewModel.stateFlow.collect { state ->
                        // 로딩 상태에 따라 프로그레스 바 등의 UI를 업데이트합니다.
                        if (state.loading) {
                            // 로딩 중이므로 프로그레스 바를 보여줍니다.
                            Log.d("login","로딩")
                        } else {
                            // 로딩이 완료되었으므로 프로그레스 바를 숨깁니다.
                            Log.d("login","로딩 끝")
                        }

                        // 에러가 발생한 경우 토스트 메시지를 표시합니다.
                        if (state.error) {
                            Log.d("login","에러")
                        }

                        // 토큰을 받아온 경우 해당 토큰을 사용하여 다음 작업을 수행합니다.
                        state.tokens?.apply {

                            // 받아온 토큰을 사용하여 다음 작업을 수행합니다.
                            App.token_prefs.accessToken = accessToken
                            App.token_prefs.refreshToken = refreshToken

//                            Log.d("login", viewModel.getLogin())
                            Log.d("login",viewModel.stateFlow.value.tokens?.accessToken.toString())

                            MySharedPreferences.setUserEmail(this@LoginActivity, email)
                            MySharedPreferences.setUserPlatform(this@LoginActivity, "KAKAO")



                            Toast.makeText(this@LoginActivity, "$email 계정으로 로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                            startActivity<MainActivity>()}
                    }
                }



            }
        }
    }
}