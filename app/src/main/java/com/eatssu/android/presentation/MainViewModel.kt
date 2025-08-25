package com.eatssu.android.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.domain.repository.UserRepository
import com.eatssu.android.domain.usecase.user.GetUserInfoUseCase
import com.eatssu.android.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState: MutableStateFlow<MainState> = MutableStateFlow(MainState())
    val uiState: StateFlow<MainState> = _uiState.asStateFlow()

//    init {
//        checkNameNull()
//    } 얘 떄문에 두번씩 처리됨.

    init{
        getUserDepartment()
    }

    fun fetchAndCheckNickname() {
        viewModelScope.launch {
            getUserInfoUseCase().onStart {
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

                // 단과대 추론
                // TODO ViewModel이 MySharedPreferences를 직접 다루면 저장소 계층(LocalDataSource)와 UI 계층이 강하게 결합. repository를 통해 처리하는게 좋음
                MySharedPreferences.setUserCollege(context,college)
                MySharedPreferences.setUserDepartment(context, department)

                if (college.collegeName.isBlank() || department.departmentName.isBlank()) {
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

                // 뷰모델이랑 도메인에서 context를 알고 있는 것도 아니고..
                // MySharedPreferences을 직접적으로 사용하는 것도 아닌데 지금 다 이렇게 되어있음
                // 지금은 어쩔수없는데 MySharedPreferences 가서 TODO 봐주세요

                // 학과 정보 불러온 뒤 학과가 포함된 단과대 정보 가져오기

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