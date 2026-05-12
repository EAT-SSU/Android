package com.eatssu.android.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.analytics.AnalyticsIdentityManager
import com.eatssu.android.domain.usecase.auth.LoginUseCase
import com.eatssu.android.domain.usecase.auth.SetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.SetRefreshTokenUseCase
import com.eatssu.android.domain.usecase.user.SetUserEmailUseCase
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.analytics.LoginAnalyticsEvent
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.UiText
import com.eatssu.common.enums.DeviceType
import com.eatssu.common.enums.ToastType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val setAccessTokenUseCase: SetAccessTokenUseCase,
    private val setRefreshTokenUseCase: SetRefreshTokenUseCase,
    private val setUserEmailUseCase: SetUserEmailUseCase,
    private val analyticsTracker: AnalyticsTracker,
    private val analyticsIdentityManager: AnalyticsIdentityManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<LoginState>>(UiState.Init)
    val uiState: StateFlow<UiState<LoginState>> = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    fun getKakaoLogin(email: String, providerID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = UiState.Loading

            val token = loginUseCase(email, providerID, DeviceType.ANDROID) ?: run {
                _uiState.value = UiState.Error
                _uiEvent.emit(
                    UiEvent.ShowToast(
                        UiText.StringResource(R.string.toast_login_failed),
                        ToastType.ERROR
                    )
                )
                return@launch
            }

            setAccessTokenUseCase(token.accessToken)
            setRefreshTokenUseCase(token.refreshToken)
            setUserEmailUseCase(email)
            analyticsIdentityManager.identifyUser(email = email)
            analyticsTracker.track(LoginAnalyticsEvent.Completed(LoginAnalyticsEvent.Method.KAKAO))

            _uiState.value = UiState.Success(LoginState.LoginSuccess)
        }
    }

    fun setInitState() {
        _uiState.value = UiState.Init
    }

    fun setLoadingState() {
        _uiState.value = UiState.Loading
    }
}

// 상태 및 이벤트 정의
sealed class LoginState {
    object LoginSuccess : LoginState()
}
