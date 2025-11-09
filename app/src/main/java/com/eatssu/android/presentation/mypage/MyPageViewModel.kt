package com.eatssu.android.presentation.mypage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.BuildConfig
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.data.repository.PreferencesRepository
import com.eatssu.android.domain.usecase.alarm.AlarmUseCase
import com.eatssu.android.domain.usecase.alarm.SetDailyNotificationStatusUseCase
import com.eatssu.android.domain.usecase.user.GetUserNickNameUseCase
import com.eatssu.android.presentation.UiEvent
import com.eatssu.android.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val getUserNickNameUseCase: GetUserNickNameUseCase,
    private val setNotificationStatusUseCase: SetDailyNotificationStatusUseCase,
    private val alarmUseCase: AlarmUseCase,
    private val preferencesRepository: PreferencesRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // 내부는 항상 "값 그 자체"만 들고 있고,
    // 화면엔 UiState로 감싸서 노출
    // 로컬 저장소에서 닉네임을 먼저 읽어서 초기 상태 설정
    private val _state = MutableStateFlow(
        MyPageState(
            appVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            nickname = MySharedPreferences.getUserName(context).takeIf {
                it.isNotBlank()
            }
        )
    )
    val uiState: StateFlow<UiState<MyPageState>> =
        _state
            .map { UiState.Success(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Init)

    // 이벤트 버퍼를 주면 토스트 연속 발생 시 유실을 줄일 수 있음
    private val _uiEvent = MutableSharedFlow<UiEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    init {
        observeNotificationStatus()
        fetchMyInfo()
    }

    private fun observeNotificationStatus() {
        viewModelScope.launch {
            preferencesRepository.dailyNotificationStatus.collectLatest { isOn ->
                _state.update { it.copy(isAlarmOn = isOn) }
            }
        }
    }

    fun fetchMyInfo() {
        viewModelScope.launch {
            val nickname = getUserNickNameUseCase()

            if (nickname.isBlank()) {
                _state.update { it.copy(nickname = null) }
                _uiEvent.emit(UiEvent.ShowToast("닉네임을 설정해주세요."))
                return@launch
            }

            _state.update { it.copy(nickname = nickname) }
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
