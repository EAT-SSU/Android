package com.eatssu.android.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.domain.repository.UserRepository
import com.eatssu.android.domain.usecase.auth.LogoutUseCase
import com.eatssu.android.domain.usecase.user.GetUserCollegeDepartmentUseCase
import com.eatssu.android.domain.usecase.user.GetUserNickNameUseCase
import com.eatssu.android.domain.usecase.user.SetUserCollegeDepartmentUseCase
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.UiText
import com.eatssu.common.enums.ToastType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val getUserNickNameUseCase: GetUserNickNameUseCase,
    private val setUserCollegeDepartmentUseCase: SetUserCollegeDepartmentUseCase,
    private val userRepository: UserRepository,
    private val getUserCollegeDepartmentUseCase: GetUserCollegeDepartmentUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<MainState>> = MutableStateFlow(UiState.Init)
    val uiState: StateFlow<UiState<MainState>> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    init {
        viewModelScope.launch {
            loadStoredUserDepartment()
            getUserDepartment()
            fetchAndCheckNickname()
        }
    }

    fun refreshUserDepartment() {
        viewModelScope.launch {
            val userInfo = getUserCollegeDepartmentUseCase()
            _uiState.value = UiState.Success(
                MainState.DepartmentState(
                    departmentName = userInfo.userDepartment.departmentName
                )
            )
        }
    }

    private suspend fun fetchAndCheckNickname() {
        val nickname = getUserNickNameUseCase()

        if (nickname.isBlank()) {
            _uiState.value = UiState.Success(MainState.NicknameNull)
            _uiEvent.emit(UiEvent.ShowToast(UiText.StringResource(R.string.set_nickname), ToastType.ERROR))
        }
    }

    fun logOut() {
        viewModelScope.launch {
            logoutUseCase()
            _uiState.value = UiState.Success(MainState.LoggedOut)
            _uiEvent.emit(
                UiEvent.ShowToast(
                    UiText.StringResource(R.string.toast_logout_success), ToastType.SUCCESS
                )
            )
        }
    }

    private val data = MutableLiveData<LocalDate>()

    fun setData(dataToSend: LocalDate) {
        data.value = dataToSend

        Timber.d("setdata $dataToSend")
    }

    fun getData(): LiveData<LocalDate> {
        return data
    }

    private suspend fun loadStoredUserDepartment() {
        val userInfo = getUserCollegeDepartmentUseCase()
        _uiState.value = UiState.Success(
            MainState.DepartmentState(
                departmentName = userInfo.userDepartment.departmentName,
                showUserDepartmentBottomSheet =
                    (userInfo.userCollege.collegeId == -1 || userInfo.userDepartment.departmentId == -1)
            )
        )
    }

    private suspend fun getUserDepartment() {
        val (college, department) = userRepository.getUserCollegeDepartment() ?: run {
            _uiEvent.emit(
                UiEvent.ShowToast(
                    UiText.StringResource(R.string.not_found),
                    ToastType.ERROR
                )
            )
            return
        }

        setUserCollegeDepartmentUseCase(college, department)

        _uiState.value = UiState.Success(
            MainState.DepartmentState(
                departmentName = department.departmentName,
                showUserDepartmentBottomSheet =
                    (college.collegeId == -1 || department.departmentId == -1)
            )
        )
    }

}


sealed class MainState {
    object NicknameNull : MainState()
    object LoggedOut : MainState()
    data class DepartmentState(
        val departmentName: String? = "",
        val showUserDepartmentBottomSheet: Boolean = false
    ) : MainState()
}
