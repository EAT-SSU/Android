package com.eatssu.android.presentation.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.BuildConfig.VERSION_CODE
import com.eatssu.android.domain.repository.FirebaseRemoteConfigRepository
import com.eatssu.android.domain.usecase.auth.GetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.GetIsAccessTokenValidUseCase
import com.eatssu.android.domain.usecase.health.HealthCheckUseCase
import com.eatssu.android.presentation.UiEvent
import com.eatssu.android.presentation.UiState
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
    private val getIsAccessTokenValidUseCase: GetIsAccessTokenValidUseCase,
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
                // 1. Firebase Remote Config 초기화
                initializeRemoteConfig()

                // 2. 버전 체크
                checkVersionUpdate()

                // 3. 자동 로그인 체크
                autoLogin()

            } catch (e: Exception) {
                Timber.e(e, "앱 초기화 중 오류 발생")
                _uiState.value = UiState.Error
                _uiEvent.emit(UiEvent.ShowToast("앱 초기화 중 오류가 발생했습니다"))
            }
        }
    }

    private suspend fun initializeRemoteConfig() {
        try {
            firebaseRemoteConfigRepository.init().fold(
                onSuccess = {
                    Timber.d("Firebase Remote Config 초기화 성공")
                },
                onFailure = { error ->
                    Timber.e(error, "Firebase Remote Config 초기화 실패")
                    // Remote Config 초기화 실패해도 앱은 계속 진행
                }
            )
        } catch (e: Exception) {
            Timber.e(e, "Firebase Remote Config 초기화 중 예외 발생")
        }
    }

    private suspend fun checkVersionUpdate() {
        try {
            val latestVersionCode = firebaseRemoteConfigRepository.getMinimumVersionCode()
            val currentVersionCode = VERSION_CODE

            val result = when {
                currentVersionCode < latestVersionCode -> VersionCheckResult.ForceUpdateRequired(
                    latestVersionCode
                )

                currentVersionCode >= latestVersionCode -> VersionCheckResult.UpdateNotRequired
                else -> VersionCheckResult.UpdateNotRequired
            }

            _versionCheckResult.value = result

            when (result) {
                is VersionCheckResult.ForceUpdateRequired -> {
                    Timber.d("강제 업데이트 필요: 최신 버전 ${result.minimumVersionCode}")
                    _uiEvent.emit(UiEvent.ShowToast("앱을 업데이트해주세요"))
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
                _uiEvent.emit(UiEvent.ShowToast("로그인이 필요합니다"))
                return@launch
            }

            // 토큰이 있어도 유효하지 않음
            if (!getIsAccessTokenValidUseCase(userAccessToken)) {
                _uiState.value = UiState.Error
                _uiEvent.emit(UiEvent.ShowToast("로그인이 필요합니다"))
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