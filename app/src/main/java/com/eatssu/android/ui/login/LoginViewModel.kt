package com.eatssu.android.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.data.dto.request.LoginWithKakaoRequest
import com.eatssu.android.data.usecase.auth.LoginUseCase
import com.eatssu.android.data.usecase.auth.SetAccessTokenUseCase
import com.eatssu.android.data.usecase.auth.SetRefreshTokenUseCase
import com.eatssu.android.data.usecase.auth.SetUserEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext private val context: Context
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
                _uiState.update {
                    it.copy(
                        loading = false, error = false,
                        toastMessage = context.getString(R.string.login_done)
                    )
                    //Todo 로그인과 회원가입에 따른 토스트 메시지 구분하기
                }

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