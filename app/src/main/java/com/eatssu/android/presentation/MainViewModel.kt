package com.eatssu.android.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.domain.repository.UserRepository
import com.eatssu.android.domain.usecase.auth.LogoutUseCase
import com.eatssu.android.domain.usecase.user.GetUserCollegeDepartmentUseCase
import com.eatssu.android.domain.usecase.user.GetUserNickNameUseCase
import com.eatssu.android.domain.usecase.user.SetUserCollegeDepartmentUseCase
import com.eatssu.android.presentation.util.ToastType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val getUserCollegeDepartmentUseCase: GetUserCollegeDepartmentUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<MainState>> = MutableStateFlow(
        UiState.Success(
            MainState.DepartmentState(MySharedPreferences.getUserDepartmentName(context))
        )
    )
    val uiState: StateFlow<UiState<MainState>> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    init {
        getUserDepartment()
        fetchAndCheckNickname()
    }

    fun refreshUserDepartment() {
        val userInfo = getUserCollegeDepartmentUseCase()
        _uiState.value = UiState.Success(
            MainState.DepartmentState(
                departmentName = userInfo.userDepartment.departmentName
            )
        )
    }

    private fun fetchAndCheckNickname() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val nickname = getUserNickNameUseCase()

            // 1) 닉네임 없음
            if (nickname.isBlank()) {
                _uiState.value = UiState.Success(MainState.NicknameNull)
                _uiEvent.emit(UiEvent.ShowToast(context.getString(R.string.set_nickname), ToastType.ERROR))
                return@launch
            }

            // 2) 정상 닉네임
            _uiState.value = UiState.Success(MainState.NicknameExists(nickname))
        }
    }

    fun logOut() {
        viewModelScope.launch {
            logoutUseCase()
            _uiState.value = UiState.Success(MainState.LoggedOut)
            _uiEvent.emit(
                UiEvent.ShowToast(
                    context.getString(R.string.toast_logout_success), ToastType.SUCCESS
                )
            )
        }
    }

    private val data = MutableLiveData<LocalDate>()

    fun setData(dataToSend: LocalDate) {
        data.value = dataToSend

        Timber.d("setdata", dataToSend.toString())
    }

    fun getData(): LiveData<LocalDate> {
        return data
    }

    private fun getUserDepartment() {
        viewModelScope.launch {
            val (college, department) = userRepository.getUserCollegeDepartment() ?: run {
                _uiState.value = UiState.Error
                _uiEvent.emit(
                    UiEvent.ShowToast(
                        context.getString(R.string.not_found),
                        ToastType.ERROR
                    )
                )
                return@launch
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
}


sealed class MainState {
    object NicknameNull : MainState()
    data class NicknameExists(val nickname: String) : MainState()
    object LoggedOut : MainState()
    data class DepartmentState(
        val departmentName: String = "",
        val showUserDepartmentBottomSheet: Boolean = false
    ) : MainState()
}
