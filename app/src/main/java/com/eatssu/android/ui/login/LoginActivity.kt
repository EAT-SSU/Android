package com.eatssu.android.ui.login

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.base.BaseActivity
import com.eatssu.android.databinding.ActivitySocialLoginBinding
import com.eatssu.android.ui.main.MainActivity
import com.eatssu.android.util.extension.showToast
import com.eatssu.android.util.extension.startActivity
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class LoginActivity :
    BaseActivity<ActivitySocialLoginBinding>(ActivitySocialLoginBinding::inflate) {

    private val loginViewModel: LoginViewModel by viewModels()

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

        setOnClickListener()
    }


    fun setOnClickListener() {
        val context = this
        binding.mcvKakaoLogin.setOnClickListener {

            Timber.d("버튼 클릭")
            lifecycleScope.launch {
                try {
                    // 서비스 코드에서는 간단하게 로그인 요청하고 oAuthToken 을 받아올 수 있다.
                    val oAuthToken = UserApiClient.loginWithKakao(context)
                    Timber.d("beanbean > $oAuthToken")
                    postUserInfo()

                } catch (error: Throwable) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        Timber.d("사용자가 명시적으로 취소")
                    } else {
                        Timber.e(error, "인증 에러 발생")
                    }
                }
            }
        }
    }


    private fun postUserInfo() {
        UserApiClient.instance.me { user, error ->
            if (user != null) {
                // 유저의 아이디
                Timber.d("invoke: id =" + user.id)
                val providerID = user.id.toString()
                // 유저의 이메일
                Timber.d("invoke: email =" + user.kakaoAccount!!.email)
                val email = user.kakaoAccount!!.email.toString()

                loginViewModel.getLogin(email, providerID)

                lifecycleScope.launch {
                    loginViewModel.uiState.collectLatest {
                        if (!it.error && !it.loading) {
                            Timber.d(it.toString())
                            showToast(it.toastMessage)
                            startActivity<MainActivity>()
                        }
                    }
                }
            }
        }
    }
}