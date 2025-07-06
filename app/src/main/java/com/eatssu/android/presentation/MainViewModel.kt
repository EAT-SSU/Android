package com.eatssu.android.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.domain.repository.UserRepository
import com.eatssu.android.domain.usecase.auth.GetUserInfoUseCase
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

    fun checkNameNull() {
        viewModelScope.launch {
            getUserInfoUseCase().onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update {
                    it.copy(
                        error = true,
                        toastMessage = context.getString(R.string.not_found)
                    )
                }
                Timber.e(e.toString())
            }.collectLatest { result ->
                Timber.d(result.toString())
                result.result?.apply {
                    if (this.nickname.isNullOrBlank()) {
                        _uiState.update {
                            it.copy(
                                isNicknameNull = true,
                                toastMessage = context.getString(R.string.set_nickname)
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isNicknameNull = false,
                                toastMessage = String.format(
                                    context.getString(R.string.hello_user),
                                    this.nickname
                                )
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
                    toastMessage = context.getString(R.string.logout_description),
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
                userRepository.getUserDepartment()
            }.onSuccess {
                Timber.d("getUserDepartment: ${it}")
                MySharedPreferences.setUserMajor(context, it)
                // 뷰모델이랑 도메인에서 context를 알고 있는 것도 아니고..
                // MySharedPreferences을 직접적으로 사용하는 것도 아닌데 지금 다 이렇게 되어있음
                // 지금은 어쩔수없는데 MySharedPreferences 가서 TODO 봐주세요
            }.onFailure { it ->
                Timber.e("getUserDepartment failed: ${it.message}")
                _uiState.update {
                    it.copy(
                        error = true,
                        toastMessage = context.getString(R.string.not_found)
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
)