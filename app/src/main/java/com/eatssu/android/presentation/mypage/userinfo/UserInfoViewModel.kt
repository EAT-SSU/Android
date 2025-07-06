package com.eatssu.android.presentation.mypage.userinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.model.UserInfo
import com.eatssu.android.domain.usecase.auth.GetUserNameUseCase
import com.eatssu.android.domain.usecase.auth.SetUserInfoUseCase
import com.eatssu.android.domain.usecase.auth.ValidateUserNameUseCase
import com.eatssu.android.domain.repository.UserRepository
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
    private val setUserInfoUseCase: SetUserInfoUseCase,
    private val getUserNameUseCase: GetUserNameUseCase,
    private val validateUserNameUseCase: ValidateUserNameUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<UserNameChangeState> =
        MutableStateFlow(UserNameChangeState())
    val uiState: StateFlow<UserNameChangeState> = _uiState.asStateFlow()

    fun loadUserInfo() {
        viewModelScope.launch {
            val userInfo = getUserNameUseCase()
            _uiState.update {
                it.copy(
                    nickname = userInfo.nickname,
                    originalNickname = userInfo.nickname,
                    selectedCollege = userInfo.college,
                    originalCollege = userInfo.college,
                    selectedMajor = userInfo.major,
                    originalMajor = userInfo.major,
                    isEnableName = true,
                    loading = false
                )
            }
        }
    }

    fun checkNickname(inputNickname: String) {
        viewModelScope.launch {
            validateUserNameUseCase(inputNickname).onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update { it.copy(error = true, toastMessage = "닉네임 중복 확인에 실패했습니다.") }
                Timber.e(e.toString())
            }.collectLatest { result ->
                if (result.result == true) {
                    _uiState.update {
                        it.copy(
                            isEnableName = true,
                            toastMessage = "사용가능한 닉네임 입니다.",
                            nickname = inputNickname,
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isEnableName = false,
                            toastMessage = "이미 사용 중인 닉네임 입니다.",
                            nickname = inputNickname,
                        )
                    }
                }
            }
        }
    }

    fun changeUserInfo() {
        val nickname = _uiState.value.nickname
        val college = _uiState.value.selectedCollege
        val major = _uiState.value.selectedMajor

        viewModelScope.launch {
            setUserInfoUseCase(UserInfo(nickname, college, major))
                .onStart { _uiState.update { it.copy(loading = true) } }
                .onCompletion { _uiState.update { it.copy(loading = false) } }
                .catch {
                    _uiState.update { it.copy(error = true, toastMessage = "정보 저장에 실패했습니다.") }
                    Timber.e(it)
                }
                .collectLatest {
                    _uiState.update { it.copy(isDone = true, toastMessage = "정보가 성공적으로 저장되었습니다.") }
                }
        }
    }

    fun updateNickname(nickname: String) {
        _uiState.update {
            val changed = nickname != it.originalNickname ||
                    it.selectedCollege != it.originalCollege ||
                    it.selectedMajor != it.originalMajor

            it.copy(nickname = nickname, isChanged = changed)
        }
    }

    fun updateCollege(college: String) {
        _uiState.update {
            val changed = it.nickname != it.originalNickname ||
                    college != it.originalCollege ||
                    it.selectedMajor != it.originalMajor

            it.copy(selectedCollege = college, isChanged = changed)
        }
    }

    fun updateMajor(major: String) {
        _uiState.update {
            val changed = it.nickname != it.originalNickname ||
                    it.selectedCollege != it.originalCollege ||
                    major != it.originalMajor

            it.copy(selectedMajor = major, isChanged = changed)
        }
    }

    fun getTotalColleges(): List<String> = userRepository.getTotalColleges()

    fun getTotalMajors(college: String): List<String> = userRepository.getTotalMajors(college)

    companion object {
        val TAG = "UserNameChangeViewModel"
    }
}

data class UserNameChangeState(
    var loading: Boolean = true,
    var error: Boolean = false,

    var toastMessage: String = "",

    var nickname: String = "",
    var selectedCollege: String = "",
    var selectedMajor: String = "",

    var isEnableName: Boolean = false,
    var isDone: Boolean = false,

    var originalNickname: String = "",
    var originalCollege: String = "",
    var originalMajor: String = "",

    var isChanged: Boolean = false,
)
