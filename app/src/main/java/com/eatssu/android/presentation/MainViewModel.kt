package com.eatssu.android.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.repository.UserRepository
import com.eatssu.android.domain.usecase.user.GetUserNickNameUseCase
import com.eatssu.android.domain.usecase.auth.LogoutUseCase
import com.eatssu.android.domain.usecase.user.GetUserCollegeDepartmentUseCase
import com.eatssu.android.domain.usecase.user.SetUserCollegeDepartmentUseCase
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
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val getUserNickNameUseCase: GetUserNickNameUseCase,
    private val setUserCollegeDepartmentUseCase: SetUserCollegeDepartmentUseCase,
    private val userRepository: UserRepository,
    private val getUserCollegeDepartmentUseCase: GetUserCollegeDepartmentUseCase,
    ) : ViewModel() {

    private val _uiState: MutableStateFlow<MainState> = MutableStateFlow(MainState())
    val uiState: StateFlow<MainState> = _uiState.asStateFlow()

//    init {
//        checkNameNull()
//    } 얘 떄문에 두번씩 처리됨.

    init{
        getUserDepartment()
    }

    fun refreshUserDepartment() {
        val userInfo = getUserCollegeDepartmentUseCase()
        Timber.d("학과 정보     갱신: ${userInfo.userCollege.collegeName}, ${userInfo.userDepartment.departmentName}")

        _uiState.update {
            it.copy(
                departmentName = userInfo.userDepartment.departmentName,
            )
        }
    }

    fun fetchAndCheckNickname() {
        viewModelScope.launch {
            getUserNickNameUseCase().onStart {
                _uiState.update { it.copy(loading = true) }
            }.catch { e ->
                _uiState.update {
                    it.copy(
                        error = true,
                        toastMessage = "정보를 불러올 수 없습니다."
                    )
                }
                Timber.e(e.toString())
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = false) }
            }.collectLatest { result ->
                Timber.d(result.toString())
                result.result?.apply {
                    if (this.nickname.isNullOrBlank()) {
                        _uiState.update {
                            it.copy(
                                isNicknameNull = true,
                                toastMessage = "닉네임을 설정해주세요."
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isNicknameNull = false,
                                toastMessage = "${this.nickname}님 반갑습니다!"
                            )
                        }
                    }
                }
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            logoutUseCase() //Todo 반환값이 쓰이는게 아니면 이렇게 해도 되나?

            _uiState.update {
                it.copy(
                    toastMessage = "로그아웃 되었습니다.",
                    isLoggedOut = true
                )
            }
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

    private fun getUserDepartment(){
        viewModelScope.launch {
            runCatching {
                userRepository.getUserCollegeDepartment()
            }.onSuccess { it ->
                val college = it.first
                val department = it.second

                Timber.d("userCollege: ${it.first}")
                Timber.d("userDepartment: ${it.second}")

                setUserCollegeDepartmentUseCase(college, department)

                if (college.collegeId == -1 || department.departmentId == -1) {
                    _uiState.update {
                        it.copy(
                            showUserDepartmentBottomSheet = true,
                        )
                    }
                }

                _uiState.update {
                    it.copy(
                        departmentName = department.departmentName,
                    )
                }
            }.onFailure { it ->
                Timber.e("getUserDepartment failed: ${it.message}")
                _uiState.update {
                    it.copy(
                        error = true,
                        toastMessage = "정보를 불러올 수 없습니다."
                    )
                }
            }
        }
    }
}


data class MainState(
    var loading: Boolean = true,
    var error: Boolean = false,
    var toastMessage: String = "",
    var isNicknameNull: Boolean = false,
    var isLoggedOut: Boolean = false,
    var showUserDepartmentBottomSheet: Boolean = false,
    var departmentName: String = "",
)