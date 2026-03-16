package com.eatssu.android.domain.usecase.alarm

import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class AlarmNotificationStatusUseCasesBehaviorSpec : AppBehaviorSpec({

    given("GetDailyNotificationStatusUseCase") {
        val settingDataStore = mockk<SettingDataStore>()
        val useCase = GetDailyNotificationStatusUseCase(settingDataStore)

        `when`("저장된 상태가 true면") {
            every { settingDataStore.dailyNotificationStatus } returns flowOf(true)

            then("true를 emit하는 flow를 반환한다") {
                runTest {
                    useCase().collect { status ->
                        status shouldBe true
                    }
                }
            }
        }

        `when`("저장된 상태가 false면") {
            every { settingDataStore.dailyNotificationStatus } returns flowOf(false)

            then("false를 emit하는 flow를 반환한다") {
                runTest {
                    useCase().collect { status ->
                        status shouldBe false
                    }
                }
            }
        }
    }

    given("SetDailyNotificationStatusUseCase") {
        val settingDataStore = mockk<SettingDataStore>()
        val useCase = SetDailyNotificationStatusUseCase(settingDataStore)
        coJustRun { settingDataStore.setDailyNotificationStatus(any()) }

        `when`("상태를 전달하면") {
            then("SettingDataStore에 그대로 위임한다") {
                runTest {
                    useCase(true)
                    coVerify(exactly = 1) { settingDataStore.setDailyNotificationStatus(true) }
                }
            }
        }
    }
})
