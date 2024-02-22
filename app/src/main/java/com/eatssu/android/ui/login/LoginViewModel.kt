package com.eatssu.android.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.dto.request.LoginWithKakaoRequestDto
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.TokenResponseDto
import com.eatssu.android.data.repository.OauthRepository
import com.eatssu.android.data.repository.UserRepository
import com.eatssu.android.data.service.OauthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
//    private val savedStateHandle: SavedStateHandle,
    private val repository: OauthRepository): ViewModel() {

    private val _stateFlow: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    val stateFlow: StateFlow<LoginState> = _stateFlow.asStateFlow()

    suspend fun getLogin(email: String, providerID: String) {
        viewModelScope.launch {

            repository.kakaoLogin(LoginWithKakaoRequestDto(email, providerID)).onStart {
                _stateFlow.update { state ->
                    state.copy(loading = true)
                }
            }.catch {
                _stateFlow.update { state ->
                    state.copy(
                        loading = false,
                        error = true,
                        toastMessage = "에러 입니다."
                    )
                }
            }.collectLatest { result ->
                if (result.isSuccess) {
                    _stateFlow.update {
                        it.copy(
                            loading = false,
                            toastMessage = "로그인 성공 입니다.",
//                            user = result.value.asDomain(),
//                            showTutorial = false,
                        )
                    }
                    Log.d("loginvm",stateFlow.value.tokens?.accessToken.toString())

                }
            }
        }
    }
}

data class LoginState(
    val toastMessage: String = "",
    val loading: Boolean = true,
    val error: Boolean = false,
    val tokens : TokenResponseDto? = null
)