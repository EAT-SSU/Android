package com.eatssu.android.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.analytics.AnalyticsIdentityManager
import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.domain.repository.UserRepository
import com.eatssu.android.domain.usecase.auth.LogoutUseCase
import com.eatssu.android.domain.usecase.user.GetUserCollegeDepartmentUseCase
import com.eatssu.android.domain.usecase.user.GetUserEmailUseCase
import com.eatssu.android.domain.usecase.user.GetUserNickNameUseCase
import com.eatssu.android.domain.usecase.user.SetUserCollegeDepartmentUseCase
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.UiText
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.analytics.ClickMyPageMenuEvent
import com.eatssu.common.analytics.ClickPlzNotMeEvent
import com.eatssu.common.enums.ToastType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    private val getUserEmailUseCase: GetUserEmailUseCase,
    private val analyticsIdentityManager: AnalyticsIdentityManager,
    private val analyticsTracker: AnalyticsTracker,
    private val settingDataStore: SettingDataStore,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<MainState>> = MutableStateFlow(UiState.Init)
    val uiState: StateFlow<UiState<MainState>> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    init {
        viewModelScope.launch {
            loadStoredUserDepartment()
            syncLanguageState()
            loadUserDepartmentFromServer()
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

    fun analyticsPlzNotMe() {
        viewModelScope.launch {
            val userCollegeDepartment = getUserCollegeDepartmentUseCase()
            val newDepartmentId = userCollegeDepartment.userDepartment.departmentId.toLong()
            val newCollegeId = userCollegeDepartment.userCollege.collegeId.toLong()

            analyticsTracker.track(
                ClickPlzNotMeEvent(
                    college = newDepartmentId,
                    major = newCollegeId,
                ),
            )

        }
    }

    fun trackMyPageMenu(menu: String) {
        viewModelScope.launch {
            val userCollegeDepartment = getUserCollegeDepartmentUseCase()
            analyticsTracker.track(
                ClickMyPageMenuEvent(
                    college = userCollegeDepartment.userCollege.collegeId.toLong(),
                    major = userCollegeDepartment.userDepartment.departmentId.toLong(),
                    menu = menu,
                ),
            )
    fun refreshUserDepartmentFromServer() {
        viewModelScope.launch {
            loadUserDepartmentFromServer()
        }
    }

    private suspend fun fetchAndCheckNickname() {
        val nickname = getUserNickNameUseCase()

        if (nickname.isBlank()) {
            _uiState.value = UiState.Success(MainState.NicknameNull)
            _uiEvent.emit(UiEvent.ShowToast(UiText.StringResource(R.string.set_nickname), ToastType.ERROR))
            return
        }

        syncAnalyticsIdentity()
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

    private suspend fun loadUserDepartmentFromServer() {
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
        syncAnalyticsIdentity()

        _uiState.value = UiState.Success(
            MainState.DepartmentState(
                departmentName = department.departmentName,
                showUserDepartmentBottomSheet =
                    (college.collegeId == -1 || department.departmentId == -1)
            )
        )
    }

    private suspend fun syncAnalyticsIdentity() {
        val email = getUserEmailUseCase()
        if (email.isBlank()) return

        val userInfo = getUserCollegeDepartmentUseCase()
        analyticsIdentityManager.identifyUser(
            email = email,
            nickname = userInfo.nickname,
            college = userInfo.userCollege,
            department = userInfo.userDepartment,
        )
    }

    private suspend fun syncLanguageState() {
        // 어떤 이유로 앱과 서버의 언어가 다를 수 있기 때문에, 앱 언어 설정을 서버에 전송
        val language = settingDataStore.appLanguage.first()
        userRepository.patchUserLanguage(language.code.uppercase())
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
