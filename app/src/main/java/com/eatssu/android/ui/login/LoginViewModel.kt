package com.eatssu.android.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.dto.request.LoginWithKakaoRequest
import com.eatssu.android.data.usecase.LoginUseCase
import com.eatssu.android.data.usecase.SetAccessTokenUseCase
import com.eatssu.android.data.usecase.SetRefreshTokenUseCase
import com.eatssu.android.data.usecase.SetUserEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val setAccessTokenUseCase: SetAccessTokenUseCase,
    private val setRefreshTokenUseCase: SetRefreshTokenUseCase,
    private val setUserEmailUseCase: SetUserEmailUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    fun getLogin(email: String, providerID: String) {
        viewModelScope.launch {
            loginUseCase(LoginWithKakaoRequest(email, providerID)).onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update { it.copy(error = true) }
                Timber.e(e, "kakaoLogin: ")
            }.collectLatest { result ->
                _uiState.update { it.copy(loading = false, error = false) }

                /*토큰 저장*/
                result.result?.let {

                    Timber.d(it.accessToken)

                    //헤더에 토큰 붙이기
                    setAccessTokenUseCase(it.accessToken)
                    setRefreshTokenUseCase(it.refreshToken)
                    setUserEmailUseCase(email)
                }
            }
        }
    }

}

data class LoginState(
    var toastMessage: String = "",
    var loading: Boolean = true,
    var error: Boolean = false,
)