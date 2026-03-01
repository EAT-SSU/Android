package com.eatssu.android.presentation.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.BuildConfig
import com.eatssu.android.R
import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.domain.usecase.alarm.AlarmUseCase
import com.eatssu.android.domain.usecase.alarm.SetDailyNotificationStatusUseCase
import com.eatssu.android.domain.usecase.user.GetUserNickNameUseCase
import com.eatssu.common.UiEvent
import com.eatssu.common.UiText
import com.eatssu.common.enums.ToastType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val getUserNickNameUseCase: GetUserNickNameUseCase,
    private val setNotificationStatusUseCase: SetDailyNotificationStatusUseCase,
    private val alarmUseCase: AlarmUseCase,
    private val settingDataStore: SettingDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MyPageState(
            appVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
        )
    )
    val uiState: StateFlow<MyPageState> = _uiState.asStateFlow()

    // 이벤트 버퍼를 주면 토스트 연속 발생 시 유실을 줄일 수 있음
    private val _uiEvent = MutableSharedFlow<UiEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        observeNotificationStatus()
        fetchMyInfo()
    }

    private fun observeNotificationStatus() {
        viewModelScope.launch {
            settingDataStore.dailyNotificationStatus.collectLatest { isOn ->
                _uiState.update { it.copy(isAlarmOn = isOn) }
            }
        }
    }

    fun fetchMyInfo() {
        viewModelScope.launch {
            val nickname = getUserNickNameUseCase()

            if (nickname.isBlank()) {
                _uiState.update { it.copy(nickname = null) }
                _uiEvent.emit(
                    UiEvent.ShowToast(
                        UiText.StringResource(R.string.toast_require_nickname),
                        ToastType.INFO
                    )
                )
                return@launch
            }

            _uiState.update { it.copy(nickname = nickname) }
        }
    }

    fun setNotificationOn() {
        viewModelScope.launch {
            setNotificationStatusUseCase(true)
            alarmUseCase.scheduleAlarm()
        }
    }

    fun setNotificationOff() {
        viewModelScope.launch {
            setNotificationStatusUseCase(false)
            alarmUseCase.cancelAlarm()
        }
    }
}

data class MyPageState(
    val nickname: String? = null,
    val platform: String = "KAKAO",
    val isAlarmOn: Boolean = false,
    val appVersion: String = "0.0.0"
) {
    val hasNickname: Boolean get() = !nickname.isNullOrBlank()
}
