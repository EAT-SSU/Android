package com.eatssu.android.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.App
import com.eatssu.android.data.dto.request.LoginWithKakaoRequest
import com.eatssu.android.data.dto.response.TokenResponse
import com.eatssu.android.data.usecase.LoginUseCase
import com.eatssu.android.util.MySharedPreferences
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
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
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
                Log.e(TAG, "kakaoLogin: ", e)
            }.collectLatest { result ->
                _uiState.update { it.copy(loading = false, error = false) }

                MySharedPreferences.setUserEmail(App.appContext, email)
                MySharedPreferences.setUserPlatform(App.appContext, "KAKAO")

                /*토큰 저장*/
                result.let {
                    Log.d(TAG, it.result?.accessToken.toString())

                    App.token_prefs.accessToken = it.result?.accessToken
                    App.token_prefs.refreshToken = it.result?.refreshToken //헤더에 붙일 토큰 저장
                }
//                setAccessTokenUseCase(result.accessToken)
//                setRefreshTokenUseCase(result.refreshToken)
//                _uiState.update { it.copy(isRegistered = result.isRegistered) }
            }
        }
    }

    companion object {
        const val TAG = "LoginViewModel"
    }
}

data class LoginState(
    var toastMessage: String = "",
    var loading: Boolean = true,
    var error: Boolean = false,
    var tokens: TokenResponse? = null,
)