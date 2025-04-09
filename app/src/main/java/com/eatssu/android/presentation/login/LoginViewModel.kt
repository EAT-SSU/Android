package com.eatssu.android.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.data.dto.request.LoginWithKakaoRequest
import com.eatssu.android.domain.usecase.auth.LoginUseCase
import com.eatssu.android.domain.usecase.auth.SetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.SetRefreshTokenUseCase
import com.eatssu.android.domain.usecase.auth.SetUserEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


// ViewModel 구현
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val setAccessTokenUseCase: SetAccessTokenUseCase,
    private val setRefreshTokenUseCase: SetRefreshTokenUseCase,
    private val setUserEmailUseCase: SetUserEmailUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginState>(LoginState.Init)
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<LoginEvent>()
    val uiEvent: SharedFlow<LoginEvent> = _uiEvent.asSharedFlow()

    fun getKakaoLogin(email: String, providerID: String) {
        viewModelScope.launch {
            loginUseCase(LoginWithKakaoRequest(email, providerID))
                .onStart {
                    _uiState.value = LoginState.Loading
                }
                .catch { e ->
                    _uiEvent.emit(LoginEvent.ShowToast(context.getString(R.string.login_failed)))
                }
                .collect { result ->
                    result.result?.let {
                        setAccessTokenUseCase(it.accessToken)
                        setRefreshTokenUseCase(it.refreshToken)
                        setUserEmailUseCase(email)

                        _uiState.value = LoginState.Success
                        _uiEvent.emit(LoginEvent.NavigateToMain)
                        _uiEvent.emit(LoginEvent.ShowToast(context.getString(R.string.login_done)))
                    }
                }
        }
    }
}

// 상태 및 이벤트 정의
sealed class LoginState {
    object Init : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
}

sealed class LoginEvent {
    data class ShowToast(val message: String) : LoginEvent()
    object NavigateToMain : LoginEvent()
}