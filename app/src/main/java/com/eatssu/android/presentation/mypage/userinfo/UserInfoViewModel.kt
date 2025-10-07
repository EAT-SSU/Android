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
import com.eatssu.android.presentation.UiEvent
import com.eatssu.android.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class UserInfoUiState(
    val nickname: String = "",
    val selectedCollege: College = College(-1, "단과대"),
    val selectedDepartment: Department = Department(-1, "학과"),

    val originalNickname: String = "",
    val originalCollege: College = College(-1, "단과대"),
    val originalDepartment: Department = Department(-1, "학과"),

    val isEnableName: Boolean = false,
    val isDone: Boolean = false,

    val isNicknameChecked: Boolean = false,
    val isNicknameChanged: Boolean = false,
    val isCollegeChanged: Boolean = false,
    val isDepartmentChanged: Boolean = false,

    val collegeList: List<College> = emptyList(),
    val departmentList: List<Department> = emptyList(),
)

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val setUserNicknameUseCase: SetUserNicknameUseCase,
    private val getUserCollegeDepartmentUseCase: GetUserCollegeDepartmentUseCase,
    private val setUserCollegeDepartmentUseCase: SetUserCollegeDepartmentUseCase,
    private val validateUserNameUseCase: ValidateUserNameUseCase,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<UserInfoUiState>>(UiState.Init)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    init{
        loadUserInfo()
        loadCollegeList()
        val collegeId = when (val state = _uiState.value) {
            is UiState.Success -> state.data?.selectedCollege?.collegeId ?: -1
            else -> -1
        }
        loadDepartmentList(collegeId)
    }

    /**
     * UiState.Success 내부의 UserInfoUiState를 변경하는 헬퍼 함수
     * 현재 상태를 Success로 바꾼 뒤 데이터 업데이트
     * UiState가 Success 상태일 때만 내부 데이터를 변경하며,
     * 그렇지 않은 경우 기본 UserInfoUiState로 초기화하여 변경함
     * */
    private fun updateUiState(transform: (UserInfoUiState) -> UserInfoUiState) {
        val currentState = _uiState.value
        _uiState.value = when (currentState) {
            is UiState.Success -> UiState.Success(transform(currentState.data ?: UserInfoUiState()))
            else -> UiState.Success(transform(UserInfoUiState()))
        }
    }

    fun loadUserInfo() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching {
                getUserCollegeDepartmentUseCase()
            }.onSuccess { userInfo ->
                _uiState.value = UiState.Success(
                    UserInfoUiState(
                        nickname = userInfo.nickname,
                        originalNickname = userInfo.nickname,
                        selectedCollege = userInfo.userCollege,
                        originalCollege = userInfo.userCollege,
                        selectedDepartment = userInfo.userDepartment,
                        originalDepartment = userInfo.userDepartment,
                        isEnableName = true
                    )
                )
                Timber.d("초기 유저 정보 로드 성공: $userInfo")

            }.onFailure { e ->
                Timber.e(e, "초기 유저 정보 로드 실패")
                _uiState.value = UiState.Error
            }
        }
    }

    fun checkNicknameDuplication(inputNickname: String) {
        viewModelScope.launch {
            runCatching {
                validateUserNameUseCase(inputNickname)
            }.onSuccess { flow ->
                flow.collect { result ->
                    if (result.result == true) {
                        updateUiState {
                            it.copy(
                                nickname = inputNickname,
                                isEnableName = true,
                                isNicknameChecked = true,
                                selectedCollege = it.selectedCollege,
                                originalCollege = it.originalCollege,
                                selectedDepartment = it.selectedDepartment,
                                originalDepartment = it.originalDepartment,
                            )
                        }
                    } else {
                        updateUiState {
                            it.copy(
                                nickname = inputNickname,
                                isEnableName = false,
                                isNicknameChecked = false,
                                selectedCollege = it.selectedCollege,
                                originalCollege = it.originalCollege,
                                selectedDepartment = it.selectedDepartment,
                                originalDepartment = it.originalDepartment,
                            )
                        }
                    }
                }
            }
                .onFailure { e ->
                    Timber.e(e, "닉네임 중복 확인 실패")
                    _uiState.value = UiState.Error
                    _uiEvent.emit(UiEvent.ShowToast("닉네임 중복 확인에 실패했습니다."))
                }
        }
    }


    fun changeUserNickname() {
        viewModelScope.launch {
            val nickname = ((_uiState.value as? UiState.Success)?.data?.nickname).orEmpty()
            runCatching { setUserNicknameUseCase(nickname) }
                .onSuccess {
                    updateUiState { it.copy(isDone = true) }
                }
                .onFailure { e ->
                    Timber.e(e, "닉네임 변경 실패")
                    _uiState.value = UiState.Error
                    _uiEvent.emit(UiEvent.ShowToast("닉네임 변경에 실패했습니다."))
                }
        }
    }


    fun updateUserDepartment() {
        viewModelScope.launch {
            val state = (_uiState.value as? UiState.Success)?.data ?: return@launch
            runCatching {
                userRepository.setUserDepartment(state.selectedDepartment.departmentId)
            }.onSuccess {
                Timber.d("학과 정보 업데이트 성공")
                setUserCollegeDepartmentUseCase(state.selectedCollege, state.selectedDepartment)
                updateUiState { it.copy(isDone = true) }
            }.onFailure { e ->
                Timber.e(e, "학과 정보 업데이트 실패")
                _uiState.value = UiState.Error
                _uiEvent.emit(UiEvent.ShowToast("학과 정보 업데이트에 실패했습니다."))
            }
        }
    }

    fun updateNickname(nickname: String) {
        updateUiState {
            val changed = nickname != it.originalNickname
            it.copy(nickname = nickname, isNicknameChanged = changed)
        }
    }

    fun updateInputCollege(college: College) {
        updateUiState {
            val changed = college != it.originalCollege
            it.copy(selectedCollege = college, isCollegeChanged = changed)
        }
    }

    fun updateInputDepartment(department: Department) {
        updateUiState {
            val changed = department != it.originalDepartment
            it.copy(selectedDepartment = department, isDepartmentChanged = changed)
        }
    }

    fun shouldBlockCollegeDepartmentChange(): Boolean {
        val state = (_uiState.value as? UiState.Success)?.data ?: return false
        // 닉네임 바꾸었는데 중복 확인을 안 한 경우만 막기
        return state.isNicknameChanged && !state.isNicknameChecked
    }

    fun loadCollegeList() {
        viewModelScope.launch {
            runCatching { userRepository.getTotalColleges() }
                .onSuccess { colleges ->
                    updateUiState { it.copy(collegeList = colleges) }
                }
                .onFailure { e ->
                    Timber.e(e, "단과대 불러오기 실패")
                }
        }
    }

    fun loadDepartmentList(collegeId: Int) {
        viewModelScope.launch {
            runCatching { userRepository.getTotalDepartments(collegeId) }
                .onSuccess { departments ->
                    updateUiState { it.copy(departmentList = departments) }
                }
                .onFailure { e ->
                    Timber.e(e, "학과 불러오기 실패")
                }
        }
    }

    companion object {
        val TAG = "UserNameChangeViewModel"
    }
}