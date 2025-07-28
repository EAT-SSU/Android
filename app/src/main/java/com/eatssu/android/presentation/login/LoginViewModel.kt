package com.eatssu.android.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.data.dto.request.LoginWithKakaoRequest
import com.eatssu.android.domain.model.TokenStateManager
import com.eatssu.android.domain.usecase.auth.LoginUseCase
import com.eatssu.android.domain.usecase.auth.SetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.SetRefreshTokenUseCase
import com.eatssu.android.domain.usecase.user.SetUserEmailUseCase
import com.eatssu.android.presentation.UiEvent
import com.eatssu.android.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
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
        viewModelScope.launch {
            loginUseCase(LoginWithKakaoRequest(email, providerID))
                .onStart {
                    _uiState.value = UiState.Loading
                }
                .catch { e ->
                    _uiState.value = UiState.Error
                    _uiEvent.emit(UiEvent.ShowToast(context.getString(R.string.login_failed)))
                }
                .collect { result ->
                    result.result?.let {
                        setAccessTokenUseCase(it.accessToken)
                        setRefreshTokenUseCase(it.refreshToken)
                        setUserEmailUseCase(email)

                        _uiState.value = UiState.Success(LoginState.LoginSuccess)
                        _uiEvent.emit(UiEvent.ShowToast(context.getString(R.string.login_done)))

                        TokenStateManager.setTokenValid()
                    }
                }
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