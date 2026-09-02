package com.eatssu.android.presentation.mypage

import app.cash.turbine.test
import com.eatssu.android.R
import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.domain.usecase.alarm.AlarmUseCase
import com.eatssu.android.domain.usecase.alarm.SetDailyNotificationStatusUseCase
import com.eatssu.android.domain.usecase.user.GetUserNickNameUseCase
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.assertToast
import com.eatssu.android.test.awaitToastEvent
import com.eatssu.common.UiState
import com.eatssu.common.enums.AppLanguage
import com.eatssu.common.enums.ToastType
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class MyPageViewModelBehaviorSpec : AppBehaviorSpec({

    given("마이페이지") {
        val getUserNickNameUseCase = mockk<GetUserNickNameUseCase>()
        val setDailyNotificationStatusUseCase = mockk<SetDailyNotificationStatusUseCase>()
        val alarmUseCase = mockk<AlarmUseCase>()
        val settingDataStore = mockk<SettingDataStore>()
        val appLanguage = MutableStateFlow(AppLanguage.KOREAN)

        every { alarmUseCase.scheduleAlarm() } just Runs
        every { alarmUseCase.cancelAlarm() } just Runs
        every { settingDataStore.appLanguage } returns appLanguage
        coEvery { setDailyNotificationStatusUseCase(any()) } returns Unit

        `when`("닉네임이 비어 있으면") {
            val dailyStatus = MutableStateFlow(false)
            every { settingDataStore.dailyNotificationStatus } returns dailyStatus
            coEvery { getUserNickNameUseCase() } returns ""

            val viewModel = MyPageViewModel(
                getUserNickNameUseCase,
                setDailyNotificationStatusUseCase,
                alarmUseCase,
                settingDataStore,
            )

            then("닉네임을 null로 두고 안내 토스트를 보낸다") {
                runTest {
                    viewModel.uiState.test {
                        val stateTurbine = this
                        viewModel.uiEvent.test {
                            viewModel.fetchMyInfo()
                            val first = stateTurbine.awaitItem()
                            val state = when (first) {
                                is UiState.Success<*> -> first as UiState.Success<MyPageState>
                                else -> stateTurbine.awaitItem() as UiState.Success<MyPageState>
                            }

                            state.data.nickname shouldBe null
                            awaitToastEvent().assertToast(R.string.toast_require_nickname, ToastType.INFO)
                            cancelAndIgnoreRemainingEvents()
                        }
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("닉네임이 존재하면") {
            val dailyStatus = MutableStateFlow(true)
            every { settingDataStore.dailyNotificationStatus } returns dailyStatus
            coEvery { getUserNickNameUseCase() } returns "eatssu"

            val viewModel = MyPageViewModel(
                getUserNickNameUseCase,
                setDailyNotificationStatusUseCase,
                alarmUseCase,
                settingDataStore,
            )

            then("state에 닉네임과 알림 상태를 반영한다") {
                runTest {
                    viewModel.uiState.test {
                        viewModel.fetchMyInfo()
                        val first = awaitItem()
                        val state = when (first) {
                            is UiState.Success<*> -> first as UiState.Success<MyPageState>
                            else -> awaitItem() as UiState.Success<MyPageState>
                        }

                        state.data.nickname shouldBe "eatssu"
                        state.data.isAlarmOn shouldBe true
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("알림을 켜면") {
            val dailyStatus = MutableStateFlow(false)
            every { settingDataStore.dailyNotificationStatus } returns dailyStatus
            coEvery { getUserNickNameUseCase() } returns "eatssu"

            val viewModel = MyPageViewModel(
                getUserNickNameUseCase,
                setDailyNotificationStatusUseCase,
                alarmUseCase,
                settingDataStore,
            )

            then("저장 후 알람을 등록한다") {
                runTest {
                    viewModel.setNotificationOn()
                    advanceUntilIdle()

                    coVerify { setDailyNotificationStatusUseCase(true) }
                    verify { alarmUseCase.scheduleAlarm() }
                }
            }
        }

        `when`("알림을 끄면") {
            val dailyStatus = MutableStateFlow(true)
            every { settingDataStore.dailyNotificationStatus } returns dailyStatus
            coEvery { getUserNickNameUseCase() } returns "eatssu"

            val viewModel = MyPageViewModel(
                getUserNickNameUseCase,
                setDailyNotificationStatusUseCase,
                alarmUseCase,
                settingDataStore,
            )

            then("저장 후 알람을 해제한다") {
                runTest {
                    viewModel.setNotificationOff()
                    advanceUntilIdle()

                    coVerify { setDailyNotificationStatusUseCase(false) }
                    verify { alarmUseCase.cancelAlarm() }
                }
            }
        }

        `when`("앱 언어가 변경되면") {
            val dailyStatus = MutableStateFlow(false)
            every { settingDataStore.dailyNotificationStatus } returns dailyStatus
            coEvery { getUserNickNameUseCase() } returns "eatssu"

            val viewModel = MyPageViewModel(
                getUserNickNameUseCase,
                setDailyNotificationStatusUseCase,
                alarmUseCase,
                settingDataStore,
            )

            then("현재 언어를 state에 반영한다") {
                runTest {
                    viewModel.uiState.test {
                        awaitItem()
                        appLanguage.value = AppLanguage.JAPANESE
                        advanceUntilIdle()

                        val state = expectMostRecentItem() as UiState.Success<MyPageState>
                        state.data.selectedLanguage shouldBe AppLanguage.JAPANESE
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }
    }
})
