package com.eatssu.android.presentation.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.usecase.auth.GetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.GetIsAccessTokenValidUseCase
import com.eatssu.android.domain.usecase.health.HealthCheckUseCase
import com.eatssu.android.domain.usecase.version.CheckForceUpdateRequiredUseCase
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
    private val checkForceUpdateRequiredUseCase: CheckForceUpdateRequiredUseCase,
    private val healthCheckUseCase: HealthCheckUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val getIsAccessTokenValidUseCase: GetIsAccessTokenValidUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<IntroState>> = MutableStateFlow(UiState.Init)
    val uiState: StateFlow<UiState<IntroState>> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    init {
        checkForceUpdate()
    }

    private fun checkForceUpdate() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            // Firebase에서 강제 업데이트 필요 여부 확인
            val isUpdateRequired = checkForceUpdateRequiredUseCase()

            if (isUpdateRequired) {
                Timber.d("강제 업데이트 필요")
                _uiState.value = UiState.Success(IntroState.ForceUpdateRequired)
                return@launch
            }

            // 업데이트가 필요 없으면 자동 로그인 진행
            Timber.d("업데이트 필요 없음, 자동 로그인 진행")
            autoLogin()
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
    object ForceUpdateRequired : IntroState()
}
