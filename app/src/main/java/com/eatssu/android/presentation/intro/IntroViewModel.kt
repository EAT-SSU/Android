package com.eatssu.android.presentation.intro

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.BuildConfig.VERSION_CODE
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.domain.repository.FirebaseRemoteConfigRepository
import com.eatssu.android.domain.usecase.auth.GetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.GetIsAccessTokenValidUseCase
import com.eatssu.android.domain.usecase.auth.GetRefreshTokenUseCase
import com.eatssu.android.domain.usecase.auth.LogoutUseCase
import com.eatssu.android.domain.usecase.auth.ReissueTokenUseCase
import com.eatssu.android.domain.usecase.auth.SetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.SetRefreshTokenUseCase
import com.eatssu.android.domain.usecase.health.HealthCheckUseCase
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.enums.ToastType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val getRefreshTokenUseCase: GetRefreshTokenUseCase,
    private val reissueTokenUseCase: ReissueTokenUseCase,
    private val setAccessTokenUseCase: SetAccessTokenUseCase,
    private val setRefreshTokenUseCase: SetRefreshTokenUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository
) : ViewModel() {

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
                val refreshToken = getRefreshTokenUseCase()
                if (refreshToken.isBlank()) {
                    _uiState.value = UiState.Error
                    _uiEvent.emit(
                        UiEvent.ShowToast(
                            context.getString(R.string.toast_token_invalid),
                            ToastType.INFO
                        )
                    )
                    return@launch
                }

                Timber.d("AccessToken invalid; attempting reissue via RefreshToken")
                when (val result = reissueTokenUseCase(refreshToken)) {
                    is ApiResult.Success -> {
                        setAccessTokenUseCase(result.data.accessToken)
                        setRefreshTokenUseCase(result.data.refreshToken)
                        _uiState.value = UiState.Success(IntroState.ValidToken)
                        return@launch
                    }

                    is ApiResult.Failure -> {
                        Timber.e(
                            "Token reissue failed during autoLogin: code=${result.responseCode}, message=${result.message}"
                        )
                        if (result.responseCode == 401 || result.responseCode == 403) {
                            logoutUseCase()
                        }
                    }

                    is ApiResult.NetworkError -> {
                        Timber.w(result.exception, "Token reissue network error during autoLogin")
                    }

                    is ApiResult.UnknownError -> {
                        Timber.e(result.exception, "Token reissue unknown error during autoLogin")
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
