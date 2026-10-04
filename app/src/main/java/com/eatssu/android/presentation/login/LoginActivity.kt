package com.eatssu.android.presentation.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.eatssu.android.R
import com.eatssu.android.analytics.ProvideAnalyticsTracker
import com.eatssu.android.presentation.MainActivity
import com.eatssu.android.presentation.util.showErrorToast
import com.eatssu.android.presentation.util.showToast
import com.eatssu.android.presentation.util.startActivity
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.analytics.CredentialsEvent
import com.eatssu.design_system.theme.EatssuTheme
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ProvideAnalyticsTracker(analyticsTracker) {
                EatssuTheme {
                    val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()

                    LaunchedEffect(uiState) {
                        if (uiState is UiState.Success) {
                            startActivity<MainActivity>()
                            finishAffinity()
                        }
                    }

                    LaunchedEffect(Unit) {
                        loginViewModel.uiEvent.collect { event ->
                            when (event) {
                                is UiEvent.ShowToast -> showToast(event)
                            }
                        }
                    }

                    BackHandler {
                        finishAffinity()
                    }

                    LoginScreen(
                        isLoading = uiState is UiState.Loading,
                        onKakaoLoginClick = { handleKakaoLogin() },
                        onBrowseGoodPriceStoreClick = {
                            startActivity<com.eatssu.android.presentation.goodprice.GoodPriceMapActivity>()
                        },
                    )
                }
            }
        }
    }

    // kakao login sdk를 통해 유저 정보를 가져와 rest api 호출하는 뷰모델 함수 호출
    private fun handleKakaoLogin() {
        lifecycleScope.launch {
            analyticsTracker.track(CredentialsEvent.ClickLoginEvent("kakao"))
            try {
                loginViewModel.setLoadingState()
                val oAuthToken = UserApiClient.loginWithKakao(this@LoginActivity)
                Timber.d("Kakao login success: $oAuthToken")
                UserApiClient.instance.me { user, error ->
                    user?.let {
                        val providerID = user.id.toString()
                        val email = user.kakaoAccount?.email.toString()
                        loginViewModel.getKakaoLogin(email, providerID)
                        analyticsTracker.track(CredentialsEvent.CompleteLoginEvent("kakao"))
                    } ?: run {
                        Timber.e(error, "User info fetch failed")
                        loginViewModel.setInitState()
                        showErrorToast(R.string.toast_login_failed)
                    }
                }
            } catch (error: Throwable) {
                Timber.e(error, "Kakao login failed")
                handleKakaoLoginError(error)
            }
        }
    }

    // kakao login sdk의 error를 다룹니다.
    private fun handleKakaoLoginError(error: Throwable) {
        when {
            error is ClientError && error.reason == ClientErrorCause.Cancelled -> {
                Timber.d("User cancelled login")
                loginViewModel.setInitState()
            }

            else -> {
                Timber.e(error, "Login failed")
                loginViewModel.setInitState()
                showErrorToast(R.string.toast_login_failed)
            }
        }
    }
}
