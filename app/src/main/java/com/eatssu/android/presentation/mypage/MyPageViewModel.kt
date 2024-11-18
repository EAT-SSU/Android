package com.eatssu.android.presentation.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.BuildConfig
import com.eatssu.android.data.repository.PreferencesRepository
import com.eatssu.android.domain.usecase.alarm.AlarmUseCase
import com.eatssu.android.domain.usecase.alarm.GetDailyNotificationStatusUseCase
import com.eatssu.android.domain.usecase.alarm.SetDailyNotificationStatusUseCase
import com.eatssu.android.domain.usecase.auth.GetUserInfoUseCase
import com.eatssu.android.domain.usecase.auth.LogoutUseCase
import com.eatssu.android.domain.usecase.auth.SetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.SetRefreshTokenUseCase
import com.eatssu.android.domain.usecase.auth.SignOutUseCase
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
class MyPageViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val setAccessTokenUseCase: SetAccessTokenUseCase,
    private val setRefreshTokenUseCase: SetRefreshTokenUseCase,
    private val setNotificationStatusUseCase: SetDailyNotificationStatusUseCase,
    private val getDailyNotificationStatusUseCase: GetDailyNotificationStatusUseCase,
    private val alarmUseCase: AlarmUseCase,
    private val preferencesRepository: PreferencesRepository // Assuming you're using DataStore here
) : ViewModel() {

    private val _uiState: MutableStateFlow<MyPageState> = MutableStateFlow(MyPageState())
    val uiState: StateFlow<MyPageState> = _uiState.asStateFlow()

    init {
        setAppVersion()
        getMyInfo()
        getNotificationStatus()
    }

    private fun setAppVersion() {
        viewModelScope.launch {
            _uiState.value =
                _uiState.value.copy(appVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
        }
    }

    private fun getNotificationStatus() {
        viewModelScope.launch {
            preferencesRepository.dailyNotificationStatus.collect { isAlarmOn ->
                _uiState.value = _uiState.value.copy(isAlarmOn = isAlarmOn)
            }
        }
    }

    private fun getMyInfo() {
        viewModelScope.launch {
            getUserInfoUseCase().onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update { it.copy(error = true, toastMessage = "정보를 불러올 수 없습니다.") }
                Timber.e(e.toString())
            }.collectLatest { result ->
                Timber.d(result.toString())
                result.result?.apply {
                    if (this.nickname.isNullOrBlank()) {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                error = false,
                                isNicknameNull = true,
                                toastMessage = "닉네임을 설정해주세요."
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                error = false,
                                isNicknameNull = false,
                                toastMessage = "${this.nickname} 님 환영합니다.",
                                nickname = this.nickname.toString(),
                                platform = this.provider
                            )
                        }
                    }
                }
            }
        }
    }

    fun loginOut() {
        viewModelScope.launch {
            logoutUseCase() //Todo 반환값이 쓰이는게 아니면 이렇게 해도 되나?

            _uiState.update {
                it.copy(
                    toastMessage = "로그아웃 되었습니다.",
                    isLoginOuted = true
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase().onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update { it.copy(error = true, toastMessage = "정보를 불러올 수 없습니다.") }
                Timber.e(e.toString())
            }.collectLatest { result ->
                Timber.d(result.toString())
                if (result.result == true) {
                    logoutUseCase()
                    _uiState.update {
                        it.copy(
                            isSignOuted = true,
                            toastMessage = "탈퇴가 완료되었습니다."
                        )
                    }
                }
            }
        }
    }

    fun setNotificationOn() {
        viewModelScope.launch {
            setNotificationStatusUseCase(true) //로컬 디비 저장
            alarmUseCase.scheduleAlarm() //알람 매니저
        }
    }

    fun setNotificationOff() {
        viewModelScope.launch {
            setNotificationStatusUseCase(false)
            alarmUseCase.cancelAlarm()
        }
    }



    companion object {
        val TAG = "MyPageViewModel"
    }
}


data class MyPageState(
    var loading: Boolean = true,
    var error: Boolean = false,

    var toastMessage: String = "",

    var nickname: String = "",
    var platform: String = "",
    var isAlarmOn: Boolean = false,
    var appVersion: String = "0.0.0",

    var isNicknameNull: Boolean = false,
    var isLoginOuted: Boolean = false,
    var isSignOuted: Boolean = false,
)