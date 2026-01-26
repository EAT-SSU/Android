package com.eatssu.android.presentation.intro

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.BuildConfig.VERSION_CODE
import com.eatssu.android.domain.repository.FirebaseRemoteConfigRepository
import com.eatssu.android.domain.usecase.auth.GetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.GetIsAccessTokenValidUseCase
import com.eatssu.android.domain.usecase.auth.LogoutUseCase
import com.eatssu.android.domain.usecase.auth.ReissueAndStoreResult
import com.eatssu.android.domain.usecase.auth.ReissueAndStoreTokenUseCase
import com.eatssu.android.domain.usecase.health.HealthCheckUseCase
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.enums.ToastType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val healthCheckUseCase: HealthCheckUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val getIsAccessTokenValidUseCase: GetIsAccessTokenValidUseCase,
    private val reissueAndStoreTokenUseCase: ReissueAndStoreTokenUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository
) : ViewModel() {

    private var reissueRetryCount = 0

    private companion object {
        const val MAX_REISSUE_RETRIES = 2
        const val REISSUE_RETRY_DELAY_MS = 1500L
    }

    private val _uiState: MutableStateFlow<UiState<IntroState>> = MutableStateFlow(UiState.Init)
    val uiState: StateFlow<UiState<IntroState>> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    private val _versionCheckResult = MutableStateFlow<VersionCheckResult?>(null)
    val versionCheckResult: StateFlow<VersionCheckResult?> = _versionCheckResult.asStateFlow()

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                // 1. 버전 체크 (Firebase Remote Config는 자동으로 초기화됨)
                checkVersionUpdate()

                // 2. 자동 로그인 체크
                autoLogin()

            } catch (e: Exception) {
                Timber.e(e, "앱 초기화 중 오류 발생")
                _uiState.value = UiState.Error
                _uiEvent.emit(UiEvent.ShowToast("앱 초기화 중 오류가 발생했습니다", ToastType.ERROR))
            }
        }
    }

    private suspend fun checkVersionUpdate() {
        try {
            val minimumVersionCode = firebaseRemoteConfigRepository.getMinimumVersionCode()
            val currentVersionCode = VERSION_CODE

            val result = when {
                currentVersionCode < minimumVersionCode -> VersionCheckResult.ForceUpdateRequired(
                    minimumVersionCode
                )

                currentVersionCode >= minimumVersionCode -> VersionCheckResult.UpdateNotRequired
                else -> VersionCheckResult.UpdateNotRequired
            }

            _versionCheckResult.value = result

            when (result) {
                is VersionCheckResult.ForceUpdateRequired -> {
                    Timber.d("강제 업데이트 필요: 최신 버전 ${result.minimumVersionCode}")
                    _uiEvent.emit(UiEvent.ShowToast("앱을 업데이트해주세요", ToastType.INFO))
                }

                VersionCheckResult.UpdateNotRequired -> {
                    Timber.d("업데이트 불필요")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "버전 체크 중 예외 발생")
        }
    }

    private fun autoLogin() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            // 서버와 통신 가능한지 먼저 확인
            if (!healthCheckUseCase()) {
                // 아무 State 처리 없이 Return해도 NetworkErrorEventBus로 인해 오류 페이지로 이동
                return@launch
            }

            val userAccessToken = getAccessTokenUseCase()
            if (userAccessToken.isEmpty()) {
                _uiState.value = UiState.Error
                _uiEvent.emit(
                    UiEvent.ShowToast(
                        context.getString(R.string.toast_token_invalid),
                        ToastType.INFO
                    )
                )
                return@launch
            }

            // 토큰이 있어도 유효하지 않음
            if (!getIsAccessTokenValidUseCase(userAccessToken)) {
                Timber.d("AccessToken invalid; attempting reissue")
                when (val result = reissueAndStoreTokenUseCase()) {
                    is ReissueAndStoreResult.Success -> {
                        reissueRetryCount = 0
                        _uiState.value = UiState.Success(IntroState.ValidToken)
                        return@launch
                    }

                    is ReissueAndStoreResult.MissingRefreshToken,
                    is ReissueAndStoreResult.RefreshInvalid -> {
                        reissueRetryCount = 0
                        logoutUseCase()
                    }

                    is ReissueAndStoreResult.TransientFailure -> {
                        Timber.w(
                            result.throwable,
                            "Token reissue transient failure during autoLogin: code=${result.responseCode}, message=${result.message}"
                        )

                        // A 정책: 네트워크/일시적 오류에서는 로그인 화면으로 보내지 않음.
                        // Splash에 머무르며 잠시 후 재시도.
                        if (reissueRetryCount < MAX_REISSUE_RETRIES) {
                            reissueRetryCount++
                            _uiEvent.emit(
                                UiEvent.ShowToast(
                                    context.getString(R.string.server_error_message),
                                    ToastType.INFO
                                )
                            )
                            delay(REISSUE_RETRY_DELAY_MS)
                            autoLogin()
                        } else {
                            _uiEvent.emit(
                                UiEvent.ShowToast(
                                    context.getString(R.string.server_error_message),
                                    ToastType.INFO
                                )
                            )
                        }
                        return@launch
                    }
                }

                _uiState.value = UiState.Error
                _uiEvent.emit(
                    UiEvent.ShowToast(
                        context.getString(R.string.toast_token_invalid),
                        ToastType.INFO
                    )
                )
                return@launch
            }

            // 토큰이 있고 유효함
            _uiState.value = UiState.Success(IntroState.ValidToken)
        }
    }
}

sealed class IntroState {
    object ValidToken : IntroState()
}

sealed class VersionCheckResult {
    data class ForceUpdateRequired(val minimumVersionCode: Long) : VersionCheckResult()
    object UpdateNotRequired : VersionCheckResult()
}
