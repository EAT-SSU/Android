package com.eatssu.android.presentation.mypage.userinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department
import com.eatssu.android.domain.repository.UserRepository
import com.eatssu.android.domain.usecase.user.GetUserCollegeDepartmentUseCase
import com.eatssu.android.domain.usecase.user.SetUserCollegeDepartmentUseCase
import com.eatssu.android.domain.usecase.user.SetUserNicknameUseCase
import com.eatssu.android.domain.usecase.user.ValidateUserNameUseCase
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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val setUserNicknameUseCase: SetUserNicknameUseCase,
    private val getUserCollegeDepartmentUseCase: GetUserCollegeDepartmentUseCase,
    private val setUserCollegeDepartmentUseCase: SetUserCollegeDepartmentUseCase,
    private val validateUserNameUseCase: ValidateUserNameUseCase,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UserNameChangeState> =
        MutableStateFlow(UserNameChangeState())
    val uiState: StateFlow<UserNameChangeState> = _uiState.asStateFlow()

    init{
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

    fun checkNickname(inputNickname: String) {
        viewModelScope.launch {
            validateUserNameUseCase(inputNickname).onStart {
                _uiState.update { it.copy(loading = true, nickname = inputNickname) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false) }
            }.catch { e ->
                _uiState.update { it.copy(error = true, toastMessage = "닉네임 중복 확인에 실패했습니다.") }
                Timber.e(e.toString())
            }.collectLatest { result ->
                if (result.result == true) {
                    _uiState.update {
                        it.copy(
                            isEnableName = true,
                            toastMessage = "사용가능한 닉네임 입니다.",
                            isNicknameChecked = true,
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isEnableName = false,
                            toastMessage = "이미 사용 중인 닉네임 입니다.",
                        )
                    }
                }
            }
        }
    }

    fun changeUserNickname() {
        val nickname = _uiState.value.nickname

        _uiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            try {
                setUserNicknameUseCase(nickname)
            } catch (e: Exception) {
                Timber.e(e, "닉네임 변경 실패")
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = true,
                        toastMessage = "닉네임 변경에 실패했습니다."
                    )
                }
                return@launch
            }
        }
        _uiState.update {
            it.copy(
                loading = false,
                isDone = true,
                toastMessage = "닉네임 변경에 성공했습니다."
            )
        }
    }

    fun updateUserDepartment(){
        viewModelScope.launch {
            runCatching {
                userRepository.setUserDepartment(_uiState.value.selectedDepartment.departmentId)
            }.onSuccess {
                Timber.d("학과 정보 업데이트 성공")
                _uiState.update{ it.copy(success = true) }

                val department = _uiState.value.selectedDepartment
                val college = _uiState.value.selectedCollege

                setUserCollegeDepartmentUseCase(college, department)

            }.onFailure { e ->
                Timber.e(e, "학과 정보 업데이트 실패")
                _uiState.update { it.copy(error = true, toastMessage = "학과 정보 업데이트에 실패했습니다.") }
            }
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
            runCatching {
                userRepository.getTotalColleges()
            }.onSuccess { colleges ->
                _uiState.update { it.copy(collegeList = colleges) }
            }.onFailure { e ->
                Timber.e(e, "단과대 불러오기 실패")
            }
        }
    }

    fun loadDepartmentList(collegeId: Int) {
        viewModelScope.launch {
            runCatching {
                userRepository.getTotalDepartments(collegeId)
            }.onSuccess { departments ->
                _uiState.update { it.copy(departmentList = departments) }
            }.onFailure { e ->
                Timber.e(e, "학과 불러오기 실패")
            }
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

    var collegeList: List<College> = emptyList(),
    var departmentList: List<Department> = emptyList(),
)
