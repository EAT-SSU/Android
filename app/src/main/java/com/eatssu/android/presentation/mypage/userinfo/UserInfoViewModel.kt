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
import com.eatssu.android.presentation.UiEvent
import com.eatssu.android.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _uiState = MutableStateFlow<UiState<UserInfoData>>(UiState.Init)
    val uiState: StateFlow<UiState<UserInfoData>> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadUserInfo()
        loadCollegeList()
    }

    fun onNicknameChanged(nickname: String) {
        val currentState = _uiState.value as? UiState.Success ?: return
        val trimmedNickname = nickname.trim()

        // Local Regex 검증
        val localValidationResult = localRegexValidateUserNameUseCase(trimmedNickname)

        val errorMessage = when (localValidationResult) {
            is LocalRegexValidateUserNameUseCase.ValidationResult.Invalid -> localValidationResult.message
            is LocalRegexValidateUserNameUseCase.ValidationResult.Valid -> null
        }

        val isNicknameChanged = trimmedNickname != currentState.data.originalNickname

        _uiState.update {
            UiState.Success(
                currentState.data.copy(
                    nickname = trimmedNickname,
                    isNicknameChanged = isNicknameChanged,
                    localValidationError = errorMessage,
                    isRemoteChecked = false // 닉네임이 바뀌면 중복 확인 초기화
                )
            )
        }
    }

    fun checkNicknameDuplication() {
        viewModelScope.launch {
            val currentState = _uiState.value as? UiState.Success ?: return@launch
            val currentNickname = currentState.data.nickname

            _uiState.update { UiState.Loading }

            // Remote 중복 검증
            val isAvailable = remoteValidateUserNameUseCase(currentNickname)

            _uiState.update {
                UiState.Success(
                    currentState.data.copy(
                        isRemoteChecked = true,
                        isRemoteAvailable = isAvailable
                    )
                )
            }

            // Toast 이벤트 발송
            val message = if (isAvailable) {
                "사용 가능한 닉네임입니다."
            } else {
                "이미 사용 중인 닉네임입니다."
            }
            _uiEvent.emit(UiEvent.ShowToast(message))

            Timber.d("닉네임 중복 확인: $currentNickname, 사용 가능: $isAvailable")
        }
    }

    fun selectCollege(college: College) {
        val currentState = _uiState.value as? UiState.Success ?: return
        val isCollegeChanged = college != currentState.data.originalCollege

        _uiState.update {
            UiState.Success(
                currentState.data.copy(
                    selectedCollege = college,
                    isCollegeChanged = isCollegeChanged,
                    // 단과대가 변경되면 학과 초기화
                    selectedDepartment = Department(departmentId = -1, departmentName = "학과"),
                    departmentList = emptyList()
                )
            )
        }

        // 선택된 단과대의 학과 목록 로드
        loadDepartmentList(college.collegeId)
    }

    fun selectDepartment(department: Department) {
        val currentState = _uiState.value as? UiState.Success ?: return
        val isDepartmentChanged = department != currentState.data.originalDepartment

        _uiState.update {
            UiState.Success(
                currentState.data.copy(
                    selectedDepartment = department,
                    isDepartmentChanged = isDepartmentChanged
                )
            )
        }
    }

    fun loadCollegeList() {
        viewModelScope.launch {
            val currentState = _uiState.value as? UiState.Success ?: return@launch

            val colleges = userRepository.getTotalColleges()
            _uiState.update {
                UiState.Success(currentState.data.copy(collegeList = colleges))
            }
            Timber.d("단과대 목록 로드: ${colleges.size}개")
        }
    }

    fun loadDepartmentList(collegeId: Int) {
        viewModelScope.launch {
            val currentState = _uiState.value as? UiState.Success ?: return@launch

            val departments = userRepository.getTotalDepartments(collegeId)
            _uiState.update {
                UiState.Success(currentState.data.copy(departmentList = departments))
            }
            Timber.d("학과 목록 로드: ${departments.size}개 (단과대 ID: $collegeId)")
        }
    }

    fun saveUserInfo() {
        viewModelScope.launch {
            val currentState = _uiState.value as? UiState.Success ?: return@launch

            _uiState.update { UiState.Loading }

            val data = currentState.data
            var nicknameUpdated = false
            var departmentUpdated = false

            // 닉네임 변경이 있는 경우
            if (data.isNicknameChanged) {
                val success = setUserNicknameUseCase(data.nickname)
                if (!success) {
                    _uiEvent.emit(UiEvent.ShowToast("닉네임 변경에 실패했습니다."))
                    _uiState.value = UiState.Error
                    return@launch
                }
                nicknameUpdated = true
            }

            // 학과/단과대 변경이 있는 경우
            if (data.isCollegeChanged || data.isDepartmentChanged) {
                val success = userRepository.setUserDepartment(data.selectedDepartment.departmentId)
                if (!success) {
                    _uiState.value = UiState.Error
                    return@launch
                }

                setUserCollegeDepartmentUseCase(
                    data.selectedCollege,
                    data.selectedDepartment
                )
                departmentUpdated = true
            }

            // 성공 메시지
            val message = when {
                nicknameUpdated && departmentUpdated -> "정보가 업데이트되었습니다."
                nicknameUpdated -> "닉네임이 변경되었습니다."
                departmentUpdated -> "학과 정보가 업데이트되었습니다."
                else -> "변경사항이 없습니다."
            }

            _uiEvent.emit(UiEvent.ShowToast(message))
        }
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            _uiState.update { UiState.Loading }

            val userInfo = getUserCollegeDepartmentUseCase()
            _uiState.update {
                UiState.Success(
                    UserInfoData(
                        nickname = userInfo.nickname,
                        originalNickname = userInfo.nickname,
                        selectedCollege = userInfo.userCollege,
                        originalCollege = userInfo.userCollege,
                        selectedDepartment = userInfo.userDepartment,
                        originalDepartment = userInfo.userDepartment
                    )
                )
            }

            // 초기 로드 시 해당 단과대의 학과 리스트도 불러옴
            if (userInfo.userCollege.collegeId != -1) {
                loadDepartmentList(userInfo.userCollege.collegeId)
            }

            Timber.d("초기 유저 정보: $userInfo")
        }
    }
}

// 화면에 표시할 실제 데이터
data class UserInfoData(
    // 닉네임
    val nickname: String = "",
    val originalNickname: String = "",
    val isNicknameChanged: Boolean = false,
    val localValidationError: String? = null, // Local Regex 검증 에러
    val isRemoteChecked: Boolean = false, // Remote 중복 확인 완료 여부
    val isRemoteAvailable: Boolean = false, // Remote 중복 확인 결과

    // 단과대/학과
    val selectedCollege: College = College(collegeId = -1, collegeName = "단과대"),
    val originalCollege: College = College(collegeId = -1, collegeName = "단과대"),
    val isCollegeChanged: Boolean = false,

    val selectedDepartment: Department = Department(departmentId = -1, departmentName = "학과"),
    val originalDepartment: Department = Department(departmentId = -1, departmentName = "학과"),
    val isDepartmentChanged: Boolean = false,

    // 목록
    val collegeList: List<College> = emptyList(),
    val departmentList: List<Department> = emptyList(),
) {
    // 중복 확인 버튼 활성화 조건
    val canCheckDuplication: Boolean
        get() = localValidationError == null &&
                isNicknameChanged

    // 저장 버튼 활성화 조건
    val canSave: Boolean
        get() = (isNicknameChanged && isRemoteChecked && isRemoteAvailable) ||
                (!isNicknameChanged && (isCollegeChanged || isDepartmentChanged))
}
