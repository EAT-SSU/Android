package com.eatssu.android.ui.mypage.usernamechange

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.usecase.GetUserNameUseCase
import com.eatssu.android.data.usecase.SetUserNameUseCase
import com.eatssu.android.data.usecase.ValidateUserNameUseCase
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
class UserNameChangeViewModel @Inject constructor(
    private val setUserNameUseCase: SetUserNameUseCase,
    private val getUserNameUseCase: GetUserNameUseCase,
    private val validateUserNameUseCase: ValidateUserNameUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UserNameChangeState> =
        MutableStateFlow(UserNameChangeState())
    val uiState: StateFlow<UserNameChangeState> = _uiState.asStateFlow()

    fun checkNickname(inputNickname: String) {
        viewModelScope.launch {
            validateUserNameUseCase(inputNickname).onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update { it.copy(error = true, toastMessage = "닉네임 중복 확인에 실패했습니다.") }
                Log.e(TAG, e.toString())
            }.collectLatest { result ->
                if (result.result == true) {
                    _uiState.update {
                        it.copy(
                            isEnableName = true,
                            toastMessage = "사용가능한 닉네임 입니다."
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isEnableName = false,
                            toastMessage = "이미 사용 중인 닉네임 입니다."
                        )
                    }
                }
            }
        }
    }

    fun changeNickname(inputNickname: String) {
        viewModelScope.launch {
            setUserNameUseCase(inputNickname).onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update { it.copy(error = true, toastMessage = "닉네임 설정에 실패했습니다.") }
                Log.e(TAG, e.toString())
            }.collectLatest { result ->
                _uiState.update { it.copy(isDone = true, toastMessage = "닉네임 설정에 성공했습니다.") }
            }
        }
    }


    companion object {
        val TAG = "UserNameChangeViewModel"
    }
}


data class UserNameChangeState(
    var loading: Boolean = true,
    var error: Boolean = false,

    var toastMessage: String = "",

    var nickname: String = "",

    var isEnableName: Boolean = false,
    var isDone: Boolean = false,
)