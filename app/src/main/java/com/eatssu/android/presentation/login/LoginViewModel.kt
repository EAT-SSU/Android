package com.eatssu.android.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.data.remote.dto.request.LoginWithKakaoRequest
import com.eatssu.android.domain.model.TokenStateManager
import com.eatssu.android.domain.usecase.auth.LoginUseCase
import com.eatssu.android.domain.usecase.auth.SetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.SetRefreshTokenUseCase
import com.eatssu.android.domain.usecase.user.SetUserEmailUseCase
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.enums.ToastType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<LoginState>>(UiState.Init)
    val uiState: StateFlow<UiState<LoginState>> = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    fun getKakaoLogin(email: String, providerID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = UiState.Loading

            val token = loginUseCase(LoginWithKakaoRequest(email, providerID)) ?: run {
                _uiState.value = UiState.Error
                _uiEvent.emit(
                    UiEvent.ShowToast(
                        context.getString(R.string.toast_login_failed),
                        ToastType.ERROR
                    )
                )
                return@launch
            }

            setAccessTokenUseCase(token.accessToken)
            setRefreshTokenUseCase(token.refreshToken)
            setUserEmailUseCase(email)

            _uiState.value = UiState.Success(LoginState.LoginSuccess)
            TokenStateManager.setTokenValid()
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