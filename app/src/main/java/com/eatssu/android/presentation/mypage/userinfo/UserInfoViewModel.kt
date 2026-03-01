package com.eatssu.android.presentation.mypage.userinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department
import com.eatssu.android.domain.repository.UserRepository
import com.eatssu.android.domain.usecase.user.GetUserCollegeDepartmentUseCase
import com.eatssu.android.domain.usecase.user.NicknameValidationResult
import com.eatssu.android.domain.usecase.user.SetUserCollegeDepartmentUseCase
import com.eatssu.android.domain.usecase.user.SetUserNicknameUseCase
import com.eatssu.android.domain.usecase.user.ValidateNicknameLocalUseCase
import com.eatssu.android.domain.usecase.user.ValidateNicknameServerUseCase
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.UiText
import com.eatssu.common.enums.ToastType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val validateNicknameServerUseCase: ValidateNicknameServerUseCase,
    private val validateNicknameLocalUseCase: ValidateNicknameLocalUseCase,
    private val userRepository: UserRepository,
) : ViewModel() {

    companion object {
        const val MIN_NICKNAME_LENGTH = 2
        const val MAX_NICKNAME_LENGTH = 16
    }

    private val _uiState = MutableStateFlow<UiState<UserInfoData>>(UiState.Init)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        initializeUserInfo()
    }

    private fun initializeUserInfo() {
        viewModelScope.launch {
            _uiState.update { UiState.Loading }

            val userInfo = getUserCollegeDepartmentUseCase()

            val initialCollege = userInfo.userCollege.takeUnless { it.collegeId == -1 }
            val initialDepartment = userInfo.userDepartment.takeUnless { it.departmentId == -1 }

            // 단과대 목록과 학과 목록을 먼저 모두 가져옴
            val colleges = userRepository.getTotalColleges()
            val departments =
                if (initialCollege != null)
                    userRepository.getTotalDepartments(initialCollege.collegeId)
                else
                    emptyList()

            // 모든 데이터를 한 번에 업데이트
            _uiState.update {
                UiState.Success(
                    UserInfoData(
                        nickname = userInfo.nickname,
                        originalNickname = userInfo.nickname,
                        selectedCollege = initialCollege,
                        originalCollege = initialCollege,
                        selectedDepartment = initialDepartment,
                        originalDepartment = initialDepartment,
                        collegeList = colleges,
                        departmentList = departments
                    )
                )
            }

            Timber.d("초기 유저 정보: $userInfo, 단과대: ${colleges.size}개, 학과: ${departments.size}개")
        }
    }

    fun onNicknameChanged(nickname: String) {
        val currentState = _uiState.value as? UiState.Success ?: return
        val trimmedNickname = nickname.trim()

        // Local 유효성 검증 (Regex)
        val validationResult = validateNicknameLocalUseCase(
            trimmedNickname,
            MIN_NICKNAME_LENGTH,
            MAX_NICKNAME_LENGTH
        )

        val errorMessage = when (validationResult) {
            is NicknameValidationResult.Invalid -> validationResult.message
            is NicknameValidationResult.Valid -> null
        }

        val isNicknameChanged = trimmedNickname != currentState.data.originalNickname

        _uiState.update {
            UiState.Success(
                currentState.data.copy(
                    nickname = trimmedNickname,
                    isNicknameChanged = isNicknameChanged,
                    nicknameValidationError = errorMessage,
                    isDuplicationChecked = false // 닉네임 변경 시 중복 확인 초기화
                )
            )
        }
    }

    fun checkNicknameDuplication() {
        viewModelScope.launch {
            val currentState = _uiState.value as? UiState.Success ?: return@launch
            val currentNickname = currentState.data.nickname

            // 서버에서 사용 가능 여부 확인
            val result = validateNicknameServerUseCase(currentNickname)

            result.onFailure { error ->
                val errorMessage = error.message?.let { UiText.DynamicString(it) }
                    ?: UiText.StringResource(R.string.nickname_error_invalid)

                _uiState.update {
                    UiState.Success(
                        currentState.data.copy(
                            nicknameValidationError = errorMessage
                        )
                    )
                }
                return@launch
            }

            _uiState.update {
                UiState.Success(
                    currentState.data.copy(
                        isDuplicationChecked = true,
                    )
                )
            }

            Timber.d("닉네임 중복 확인 성공: $currentNickname")
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
                    selectedDepartment = null,
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
                    isDepartmentChanged = isDepartmentChanged,
                )
            )
        }
    }

    fun loadDepartmentList(collegeId: Int) {
        viewModelScope.launch {
            val currentState = _uiState.value as? UiState.Success ?: return@launch

            if (collegeId == -1) {
                Timber.w("학과 목록 로드 스킵: invalid collegeId=-1")
                _uiState.update {
                    UiState.Success(currentState.data.copy(departmentList = emptyList()))
                }
                return@launch
            }

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
                val result = setUserNicknameUseCase(data.nickname)
                result.onFailure { error ->
                    _uiEvent.emit(
                        UiEvent.ShowToast(
                            UiText.StringResource(R.string.toast_nickname_change_failed),
                            ToastType.ERROR
                        )
                    )
                    _uiState.value = UiState.Error
                    return@launch
                }
                nicknameUpdated = true
            }

            // 학과/단과대 변경이 있는 경우
            if (data.isCollegeChanged || data.isDepartmentChanged) {
                val department = data.selectedDepartment ?: return@launch
                val college = data.selectedCollege ?: return@launch

                val success = userRepository.setUserDepartment(department.departmentId)
                if (!success) {
                    _uiState.value = UiState.Error
                    return@launch
                }

                setUserCollegeDepartmentUseCase(college, department)
                departmentUpdated = true
            }

            // 성공 메시지
            val message = when {
                nicknameUpdated && departmentUpdated -> UiText.StringResource(R.string.toast_info_updated)
                nicknameUpdated -> UiText.StringResource(R.string.toast_nickname_changed)
                departmentUpdated -> UiText.StringResource(R.string.toast_department_updated)
                else -> UiText.StringResource(R.string.toast_no_changes)
            }

            _uiEvent.emit(UiEvent.ShowToast(message, ToastType.INFO))
            _uiState.update {
                UiState.Success(
                    data.copy(
                        isDone = true
                    )
                )
            }
        }
    }
}

// 화면에 표시할 실제 데이터
data class UserInfoData(
    // 닉네임
    val nickname: String = "",
    val originalNickname: String = "",
    val isNicknameChanged: Boolean = false,
    val nicknameValidationError: UiText? = null, // 닉네임 검증 에러 텍스트
    val isDuplicationChecked: Boolean = false, // 중복 확인 완료 여부

    // 단과대/학과
    val selectedCollege: College? = null,
    val originalCollege: College? = null,
    val isCollegeChanged: Boolean = false,

    val selectedDepartment: Department? = null,
    val originalDepartment: Department? = null,
    val isDepartmentChanged: Boolean = false,

    // 목록
    val collegeList: List<College> = emptyList(),
    val departmentList: List<Department> = emptyList(),

    val isDone: Boolean = false,
) {
    // 중복 확인 버튼 활성화 조건
    val canCheckDuplication: Boolean
        get() = nicknameValidationError == null && // 유효성 검증 통과
                isNicknameChanged && // 닉네임 변경됨
                !isDuplicationChecked // 중복 확인 아직 안 함

    // 저장 버튼 활성화 조건
    val canSave: Boolean
        get() {
            val hasNicknameChange = isNicknameChanged
            val isNicknameValid = isDuplicationChecked && nicknameValidationError == null

            val hasDepartmentChange = isCollegeChanged || isDepartmentChanged
            val isDepartmentSelected = selectedDepartment != null

            return when {
                // 닉네임 변경: 닉네임 유효성 필수
                hasNicknameChange -> isNicknameValid
                // 학과/단과대 변경: 유효한 학과 선택 필수
                hasDepartmentChange -> isDepartmentSelected
                // 변경사항 없음
                else -> false
            }
        }
}
