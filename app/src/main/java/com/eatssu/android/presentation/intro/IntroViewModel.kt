package com.eatssu.android.presentation.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.BuildConfig.VERSION_CODE
import com.eatssu.android.data.local.AppThemePreferences
import com.eatssu.android.domain.model.AppTheme
import com.eatssu.android.domain.repository.FirebaseRemoteConfigRepository
import com.eatssu.android.domain.usecase.auth.GetAccessTokenUseCase
import com.eatssu.android.domain.usecase.health.HealthCheckUseCase
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.UiText
import com.eatssu.common.enums.ToastType
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val healthCheckUseCase: HealthCheckUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository,
    private val appThemePreferences: AppThemePreferences,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<IntroState>> = MutableStateFlow(UiState.Init)
    val uiState: StateFlow<UiState<IntroState>> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    private val _versionCheckResult = MutableStateFlow<VersionCheckResult?>(null)
    val versionCheckResult: StateFlow<VersionCheckResult?> = _versionCheckResult.asStateFlow()

    private val _appTheme = MutableStateFlow(appThemePreferences.getAppTheme())
    val appTheme: StateFlow<AppTheme> = _appTheme.asStateFlow()

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                syncAppTheme()

                // 1. 버전 체크 (Firebase Remote Config는 자동으로 초기화됨)
                checkVersionUpdate()

                // 2. 자동 로그인 체크
                autoLogin()

            } catch (e: Exception) {
                Timber.e(e, "앱 초기화 중 오류 발생")
                _uiState.value = UiState.Error
                _uiEvent.emit(UiEvent.ShowToast(UiText.StringResource(R.string.toast_app_init_error), ToastType.ERROR))
            }
        }
    }

    private suspend fun syncAppTheme() {
        try {
            val remoteTheme = firebaseRemoteConfigRepository.getAppTheme()
            _appTheme.value = remoteTheme
            appThemePreferences.setAppTheme(remoteTheme)
            Timber.i("theme loaded $remoteTheme")
        } catch (e: Exception) {
            Timber.e(e, "앱 테마 로드 중 예외 발생")
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
                    _uiEvent.emit(UiEvent.ShowToast(UiText.StringResource(R.string.toast_app_update_required), ToastType.INFO))
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
                        UiText.StringResource(R.string.toast_token_invalid),
                        ToastType.INFO
                    )
                )
                return@launch
            }

            // 스플래시에서는 헬스체크만 수행. 토큰 유효성/재발급은 실제 API 요청에서 Authenticator가 처리.
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
