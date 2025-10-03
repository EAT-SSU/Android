package com.eatssu.android.presentation.login

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityLoginBinding
import com.eatssu.android.presentation.MainActivity
import com.eatssu.android.presentation.base.BaseActivity
import com.eatssu.android.presentation.util.showToast
import com.eatssu.android.presentation.util.startActivity
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.enums.ScreenId
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class LoginActivity :
    BaseActivity<ActivityLoginBinding>(
        ActivityLoginBinding::inflate,
        ScreenId.LOGIN_LOGIN
    ) {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUi()
        observeState()
        observeEvents()
    }

    private fun initUi() {
        // 툴바 숨기기
        with(toolbar) {
            visibility = View.GONE
            setSupportActionBar(this)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(false)
                setDisplayShowTitleEnabled(false)
            }
        }

        binding.ibKakaoLogin.setOnClickListener {
            handleKakaoLogin()
        }
    }

    //kakao login sdk를 통해 유저 정보를 가져와 rest api 호출하는 뷰모델 함수 호출
    private fun handleKakaoLogin() {
        lifecycleScope.launch {
            try {
                loginViewModel.setLoadingState()
                val oAuthToken = UserApiClient.loginWithKakao(this@LoginActivity)
                Timber.d("Kakao login success: $oAuthToken")
                UserApiClient.instance.me { user, error ->
                    user?.let {
                        val providerID = user.id.toString()
                        val email = user.kakaoAccount?.email.toString()
                        loginViewModel.getKakaoLogin(email, providerID)
                    } ?: Timber.e(error, "User info fetch failed")
                }
            } catch (error: Throwable) {
                handleKakaoLoginError(error)
            }
        }
    }

    //kakao login sdk의 error를 다룹니다.
    private fun handleKakaoLoginError(error: Throwable) {
        when {
            error is ClientError && error.reason == ClientErrorCause.Cancelled -> {
                Timber.d("User cancelled login")
                loginViewModel.setInitState()
            }

            else -> {
                Timber.e(error, "Login failed")
                showToast(getString(R.string.login_failed))
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> showLoading(true)
                        is UiState.Success -> {
                            startActivity<MainActivity>()
                            finishAffinity()
                        }
                        else -> {
                            showLoading(false)
                        }
                    }
                }
            }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.uiEvent.collect { event ->
                    when (event) {
                        is UiEvent.ShowToast -> showToast(event.message)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.ibKakaoLogin.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() //로그인 화면에서 뒤로 가기 눌렀을 때에는 백스택 없어야 함 (앱 종료)
    }
}
