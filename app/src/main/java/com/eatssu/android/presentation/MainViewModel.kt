package com.eatssu.android.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.domain.repository.UserRepository
import com.eatssu.android.domain.usecase.auth.LogoutUseCase
import com.eatssu.android.domain.usecase.user.GetUserCollegeDepartmentUseCase
import com.eatssu.android.domain.usecase.user.GetUserNickNameUseCase
import com.eatssu.android.domain.usecase.user.SetUserCollegeDepartmentUseCase
import com.eatssu.common.UiEvent
import com.eatssu.common.UiText
import com.eatssu.common.enums.ToastType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val getUserNickNameUseCase: GetUserNickNameUseCase,
    private val setUserCollegeDepartmentUseCase: SetUserCollegeDepartmentUseCase,
    private val userRepository: UserRepository,
    private val getUserCollegeDepartmentUseCase: GetUserCollegeDepartmentUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<MainUiState> = MutableStateFlow(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadStoredUserDepartment()
        getUserDepartment()
        fetchAndCheckNickname()
    }

    fun refreshUserDepartment() {
        viewModelScope.launch {
            val userInfo = getUserCollegeDepartmentUseCase()
            _uiState.value = MainUiState.DepartmentReady(
                departmentName = userInfo.userDepartment.departmentName
            )
        }
    }

    private fun fetchAndCheckNickname() {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading

            val nickname = getUserNickNameUseCase()

            // 1) 닉네임 없음
            if (nickname.isBlank()) {
                _uiState.value = MainUiState.NicknameNull
                _uiEvent.emit(UiEvent.ShowToast(UiText.StringResource(R.string.set_nickname), ToastType.ERROR))
                return@launch
            }

            // 2) 정상 닉네임
            _uiState.value = MainUiState.NicknameExists(nickname)
        }
    }

    fun logOut() {
        viewModelScope.launch {
            logoutUseCase()
            _uiState.value = MainUiState.LoggedOut
            _uiEvent.emit(
                UiEvent.ShowToast(
                    UiText.StringResource(R.string.toast_logout_success), ToastType.SUCCESS
                )
            )
        }
    }


    private fun loadStoredUserDepartment() {
        viewModelScope.launch {
            val userInfo = getUserCollegeDepartmentUseCase()
            _uiState.value = MainUiState.DepartmentReady(
                departmentName = userInfo.userDepartment.departmentName,
                showUserDepartmentBottomSheet =
                    (userInfo.userCollege.collegeId == -1 || userInfo.userDepartment.departmentId == -1)
            )
        }
    }

    private fun getUserDepartment() {
        viewModelScope.launch {
            val (college, department) = userRepository.getUserCollegeDepartment() ?: run {
                _uiState.value = MainUiState.Error
                _uiEvent.emit(
                    UiEvent.ShowToast(
                        UiText.StringResource(R.string.not_found),
                        ToastType.ERROR
                    )
                )
                return@launch
            }

            setUserCollegeDepartmentUseCase(college, department)

            _uiState.value = MainUiState.DepartmentReady(
                departmentName = department.departmentName,
                showUserDepartmentBottomSheet =
                    (college.collegeId == -1 || department.departmentId == -1)
            )
        }
    }
}


sealed interface MainUiState {
    data object Loading : MainUiState
    data object NicknameNull : MainUiState
    data class NicknameExists(val nickname: String) : MainUiState
    data object LoggedOut : MainUiState
    data object Error : MainUiState
    data class DepartmentReady(
        val departmentName: String? = "",
        val showUserDepartmentBottomSheet: Boolean = false,
    ) : MainUiState
}
