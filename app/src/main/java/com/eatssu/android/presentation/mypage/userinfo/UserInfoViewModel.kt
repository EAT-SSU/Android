package com.eatssu.android.presentation.mypage.userinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department
import com.eatssu.android.domain.repository.UserRepository
import com.eatssu.android.domain.usecase.user.GetUserCollegeDepartmentUseCase
import com.eatssu.android.domain.usecase.user.LocalRegexValidateUserNameUseCase
import com.eatssu.android.domain.usecase.user.RemoteValidateUserNameUseCase
import com.eatssu.android.domain.usecase.user.SetUserCollegeDepartmentUseCase
import com.eatssu.android.domain.usecase.user.SetUserNicknameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val setUserNicknameUseCase: SetUserNicknameUseCase,
    private val getUserCollegeDepartmentUseCase: GetUserCollegeDepartmentUseCase,
    private val setUserCollegeDepartmentUseCase: SetUserCollegeDepartmentUseCase,
    private val remoteValidateUserNameUseCase: RemoteValidateUserNameUseCase,
    private val localRegexValidateUserNameUseCase: LocalRegexValidateUserNameUseCase,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UserNameChangeState> =
        MutableStateFlow(UserNameChangeState())
    val uiState: StateFlow<UserNameChangeState> = _uiState.asStateFlow()

    init {
        loadUserInfo()
        loadCollegeList()
        loadDepartmentList(_uiState.value.selectedCollege.collegeId)
    }

    fun loadUserInfo() {
        viewModelScope.launch {
            val userInfo = getUserCollegeDepartmentUseCase()
            _uiState.update {
                it.copy(
                    nickname = userInfo.nickname,
                    originalNickname = userInfo.nickname,
                    selectedCollege = userInfo.userCollege,
                    originalCollege = userInfo.userCollege,
                    selectedDepartment = userInfo.userDepartment,
                    originalDepartment = userInfo.userDepartment,
                    isEnableName = true,
                    loading = false
                )
            }
            Timber.d("초기 유저 정보: $userInfo")
        }
    }

    fun checkNicknameRemote(inputNickname: String) = viewModelScope.launch {
        _uiState.update { it.copy(loading = true, nickname = inputNickname) }

        val valid = remoteValidateUserNameUseCase(inputNickname)

        if (!valid) {
            _uiState.update {
                it.copy(
                    loading = false,
                    error = false,
                    isEnableName = false,
                    toastMessage = "이미 사용 중인 닉네임 입니다.",
                )
            }
            return@launch
        }

        _uiState.update {
            it.copy(
                loading = false,
                error = false,
                isEnableName = true,
                toastMessage = "사용가능한 닉네임 입니다.",
                isNicknameChecked = true,
            )
        }
    }

    fun changeUserNickname() {
        val nickname = _uiState.value.nickname

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            val success = setUserNicknameUseCase(nickname)
            if (!success) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = true,
                        toastMessage = "닉네임 변경에 실패했습니다."
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    loading = false,
                    isDone = true,
                    toastMessage = "닉네임 변경에 성공했습니다."
                )
            }
        }
    }

    fun updateUserDepartment() {
        viewModelScope.launch {
            val success =
                userRepository.setUserDepartment(_uiState.value.selectedDepartment.departmentId)

            if (!success) {
                _uiState.update { it.copy(error = true, toastMessage = "학과 정보 업데이트에 실패했습니다.") }
                return@launch
            }

            _uiState.update { it.copy(success = true) }

            val department = _uiState.value.selectedDepartment
            val college = _uiState.value.selectedCollege

            setUserCollegeDepartmentUseCase(college, department)
        }
    }

    fun validateAndUpdateNickname(nickname: String) {
        val validationResult = localRegexValidateUserNameUseCase(nickname)
        Timber.d("$nickname 닉네임 검증 결과: $validationResult")

        val errorMessage = when (validationResult) {
            is LocalRegexValidateUserNameUseCase.ValidationResult.Invalid -> validationResult.message
            is LocalRegexValidateUserNameUseCase.ValidationResult.Valid -> null
        }

        _uiState.update {
            val changed = nickname != it.originalNickname
            it.copy(
                nickname = nickname,
                isNicknameChanged = changed,
                nicknameValidationError = errorMessage,
                isNicknameChecked = false
            )
        }
    }

    fun updateNickname(nickname: String) {
        _uiState.update {
            val changed = nickname != it.originalNickname

            it.copy(nickname = nickname, isNicknameChanged = changed)
        }
    }

    fun updateInputCollege(college: College) {
        _uiState.update {
            val changed = college != it.originalCollege

            it.copy(selectedCollege = college, isCollegeChanged = changed)
        }
    }

    fun updateInputDepartment(department: Department) {
        _uiState.update {
            val changed = department != it.originalDepartment

            it.copy(selectedDepartment = department, isDepartmentChanged = changed)
        }
    }

    fun loadCollegeList() {
        viewModelScope.launch {
            val colleges = userRepository.getTotalColleges()
            _uiState.update { it.copy(collegeList = colleges) }
        }
    }

    fun loadDepartmentList(collegeId: Int) {
        viewModelScope.launch {
            val departments = userRepository.getTotalDepartments(collegeId)
            _uiState.update { it.copy(departmentList = departments) }
        }
    }

    companion object {
        val TAG = "UserNameChangeViewModel"
    }
}

data class UserNameChangeState(
    var success: Boolean = false,
    var loading: Boolean = true,
    var error: Boolean = false,

    var toastMessage: String = "",

    var nickname: String = "",
    var selectedCollege: College = College(collegeId = -1, collegeName = "단과대"),
    var selectedDepartment: Department = Department(departmentId = -1, departmentName = "학과"),

    var isEnableName: Boolean = false,
    var isDone: Boolean = false,

    var originalNickname: String = "",
    var originalCollege: College = College(collegeId = -1, collegeName = "단과대"),
    var originalDepartment: Department = Department(departmentId = -1, departmentName = "학과"),

    var isNicknameChecked: Boolean = false,
    var isNicknameChanged: Boolean = false,
    var isCollegeChanged: Boolean = false,
    var isDepartmentChanged: Boolean = false,

    var nicknameValidationError: String? = null,

    var collegeList: List<College> = emptyList(),
    var departmentList: List<Department> = emptyList(),
)
