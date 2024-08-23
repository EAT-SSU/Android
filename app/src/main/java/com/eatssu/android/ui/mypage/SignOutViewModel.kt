package com.eatssu.android.ui.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.usecase.GetUserInfoUseCase
import com.eatssu.android.data.usecase.LogoutUseCase
import com.eatssu.android.data.usecase.SetAccessTokenUseCase
import com.eatssu.android.data.usecase.SetRefreshTokenUseCase
import com.eatssu.android.data.usecase.SignOutUseCase
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
class SignOutViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val setAccessTokenUseCase: SetAccessTokenUseCase,
    private val setRefreshTokenUseCase: SetRefreshTokenUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<SignOutState> = MutableStateFlow(SignOutState())
    val uiState: StateFlow<SignOutState> = _uiState.asStateFlow()

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase().onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update { it.copy(error = true, toastMessage = "정보를 불러올 수 없습니다.") }
                Log.d(TAG, e.toString())

            }.collectLatest { result ->
                Log.d(TAG, result.toString())
                if (result.result == true) {
                    logoutUseCase()
                    _uiState.update {
                        it.copy(
                            isSignOuted = true,
                            toastMessage = "탈퇴가 완료되었습니다."
                        )
                    }
                }
            }
        }
    }

    companion object {
        val TAG = "SignOutViewModel"
    }
}


data class SignOutState(
    var loading: Boolean = true,
    var error: Boolean = false,

    var toastMessage: String = "",

    var nickname: String = "",
    var platform: String = "",

    var isNicknameNull: Boolean = false,
    var isLoginOuted: Boolean = false,
    var isSignOuted: Boolean = false,
)