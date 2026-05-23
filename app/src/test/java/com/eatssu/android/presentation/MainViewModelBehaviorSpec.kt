package com.eatssu.android.presentation

import app.cash.turbine.test
import com.eatssu.android.R
import com.eatssu.android.analytics.AnalyticsIdentityManager
import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department
import com.eatssu.android.domain.repository.UserRepository
import com.eatssu.android.domain.usecase.auth.LogoutUseCase
import com.eatssu.android.domain.usecase.user.GetUserCollegeDepartmentUseCase
import com.eatssu.android.domain.usecase.user.GetUserEmailUseCase
import com.eatssu.android.domain.usecase.user.GetUserNickNameUseCase
import com.eatssu.android.domain.usecase.user.SetUserCollegeDepartmentUseCase
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.expectToast
import com.eatssu.android.test.sampleUserInfo
import com.eatssu.common.UiState
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.analytics.ClickMyPageMenuEvent
import com.eatssu.common.enums.AppLanguage
import com.eatssu.common.enums.ToastType
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelBehaviorSpec : AppBehaviorSpec({

    given("메인 화면") {
        val logoutUseCase = mockk<LogoutUseCase>()
        val getUserNickNameUseCase = mockk<GetUserNickNameUseCase>()
        val setUserCollegeDepartmentUseCase = mockk<SetUserCollegeDepartmentUseCase>()
        val userRepository = mockk<UserRepository>()
        val getUserCollegeDepartmentUseCase = mockk<GetUserCollegeDepartmentUseCase>()
        val getUserEmailUseCase = mockk<GetUserEmailUseCase>()
        val analyticsIdentityManager = mockk<AnalyticsIdentityManager>(relaxed = true)
        val analyticsTracker = mockk<AnalyticsTracker>(relaxed = true)
        val settingDataStore = mockk<SettingDataStore>()
        val appLanguageFlow = MutableStateFlow(AppLanguage.KOREAN)

        val college = College(collegeId = 1, collegeName = "IT")
        val department = Department(departmentId = 11, departmentName = "컴퓨터학부")
        val userInfo = sampleUserInfo(
            nickname = "eatssu",
            college = college,
            department = department,
        )

        coEvery { logoutUseCase() } returns Unit
        coEvery { getUserNickNameUseCase() } returns "eatssu"
        coEvery { getUserEmailUseCase() } returns "test@soongsil.ac.kr"
        coEvery { getUserCollegeDepartmentUseCase() } returns userInfo
        coEvery { userRepository.getUserCollegeDepartment() } returns (college to department)
        coEvery { userRepository.patchUserLanguage(any()) } returns true
        coEvery { setUserCollegeDepartmentUseCase(college, department) } returns Unit
        every { settingDataStore.appLanguage } returns appLanguageFlow

        `when`("학과 정보를 새로고침하면") {
            val viewModel = MainViewModel(
                logoutUseCase = logoutUseCase,
                getUserNickNameUseCase = getUserNickNameUseCase,
                setUserCollegeDepartmentUseCase = setUserCollegeDepartmentUseCase,
                userRepository = userRepository,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                getUserEmailUseCase = getUserEmailUseCase,
                analyticsIdentityManager = analyticsIdentityManager,
                analyticsTracker = analyticsTracker,
                settingDataStore = settingDataStore,
            )

            then("부서명이 반영된 DepartmentState로 전이된다") {
                runTest {
                    viewModel.refreshUserDepartment()
                    eventually(2.seconds) {
                        viewModel.uiState.value shouldBe UiState.Success(
                            MainState.DepartmentState(departmentName = "컴퓨터학부")
                        )
                    }
                }
            }
        }

        `when`("초기화 중 닉네임 조회가 늦게 완료되면") {
            coEvery { getUserCollegeDepartmentUseCase() } returns userInfo
            coEvery { userRepository.getUserCollegeDepartment() } returns (college to department)
            coEvery { setUserCollegeDepartmentUseCase(college, department) } returns Unit
            coEvery { getUserNickNameUseCase() } coAnswers {
                delay(1_000)
                "eatssu"
            }

            then("최종 상태는 DepartmentState로 유지된다") {
                runTest {
                    val viewModel = MainViewModel(
                        logoutUseCase = logoutUseCase,
                        getUserNickNameUseCase = getUserNickNameUseCase,
                        setUserCollegeDepartmentUseCase = setUserCollegeDepartmentUseCase,
                        userRepository = userRepository,
                        getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                        getUserEmailUseCase = getUserEmailUseCase,
                        analyticsIdentityManager = analyticsIdentityManager,
                        analyticsTracker = analyticsTracker,
                        settingDataStore = settingDataStore,
                    )

                    advanceUntilIdle()

                    viewModel.uiState.value shouldBe UiState.Success(
                        MainState.DepartmentState(
                            departmentName = "컴퓨터학부",
                            showUserDepartmentBottomSheet = false
                        )
                    )
                }
            }
        }

        `when`("저장된 학과 정보가 없는 유저로 초기화되면") {
            coEvery {
                getUserCollegeDepartmentUseCase()
            } returns sampleUserInfo(
                nickname = "eatssu",
                college = College(collegeId = -1, collegeName = "단과대"),
                department = Department(departmentId = -1, departmentName = "학과"),
            )
            coEvery { getUserNickNameUseCase() } coAnswers {
                delay(10_000)
                "eatssu"
            }
            coEvery { userRepository.getUserCollegeDepartment() } coAnswers {
                delay(1_000)
                null
            }

            then("not found 토스트를 내보낸다") {
                runTest {
                    val viewModel = MainViewModel(
                        logoutUseCase = logoutUseCase,
                        getUserNickNameUseCase = getUserNickNameUseCase,
                        setUserCollegeDepartmentUseCase = setUserCollegeDepartmentUseCase,
                        userRepository = userRepository,
                        getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                        getUserEmailUseCase = getUserEmailUseCase,
                        analyticsIdentityManager = analyticsIdentityManager,
                        analyticsTracker = analyticsTracker,
                        settingDataStore = settingDataStore,
                    )

                    viewModel.uiEvent.test {
                        advanceUntilIdle()
                        expectToast(R.string.not_found, ToastType.ERROR)
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("닉네임이 비어있는 유저로 초기화되면") {
            coEvery { getUserCollegeDepartmentUseCase() } returns userInfo
            coEvery { userRepository.getUserCollegeDepartment() } returns (college to department)
            coEvery { getUserNickNameUseCase() } coAnswers {
                delay(1_000)
                " "
            }

            then("닉네임 설정 안내 토스트를 내보낸다") {
                runTest {
                    val viewModel = MainViewModel(
                        logoutUseCase = logoutUseCase,
                        getUserNickNameUseCase = getUserNickNameUseCase,
                        setUserCollegeDepartmentUseCase = setUserCollegeDepartmentUseCase,
                        userRepository = userRepository,
                        getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                        getUserEmailUseCase = getUserEmailUseCase,
                        analyticsIdentityManager = analyticsIdentityManager,
                        analyticsTracker = analyticsTracker,
                        settingDataStore = settingDataStore,
                    )

                    viewModel.uiEvent.test {
                        advanceUntilIdle()
                        expectToast(R.string.set_nickname, ToastType.ERROR)
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("로그아웃을 수행하면") {
            val viewModel = MainViewModel(
                logoutUseCase = logoutUseCase,
                getUserNickNameUseCase = getUserNickNameUseCase,
                setUserCollegeDepartmentUseCase = setUserCollegeDepartmentUseCase,
                userRepository = userRepository,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                getUserEmailUseCase = getUserEmailUseCase,
                analyticsIdentityManager = analyticsIdentityManager,
                analyticsTracker = analyticsTracker,
                settingDataStore = settingDataStore,
            )

            then("로그아웃 유즈케이스 호출 후 성공 토스트와 LoggedOut 상태를 반영한다") {
                runTest {
                    viewModel.uiEvent.test {
                        viewModel.logOut()

                        expectToast(R.string.toast_logout_success, ToastType.SUCCESS)
                        eventually(2.seconds) {
                            coVerify { logoutUseCase() }
                            viewModel.uiState.value shouldBe UiState.Success(MainState.LoggedOut)
                        }
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("마이페이지 메뉴 클릭을 로깅하면") {
            val viewModel = MainViewModel(
                logoutUseCase = logoutUseCase,
                getUserNickNameUseCase = getUserNickNameUseCase,
                setUserCollegeDepartmentUseCase = setUserCollegeDepartmentUseCase,
                userRepository = userRepository,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
                getUserEmailUseCase = getUserEmailUseCase,
                analyticsIdentityManager = analyticsIdentityManager,
                analyticsTracker = analyticsTracker,
                settingDataStore = settingDataStore,
            )

            then("사용자 학과/단과대와 메뉴명을 이벤트에 담아 전송한다") {
                runTest {
                    viewModel.trackMyPageMenu("my_info")
                    advanceUntilIdle()

                    verify {
                        analyticsTracker.track(
                            ClickMyPageMenuEvent(
                                college = 1L,
                                major = 11L,
                                menu = "my_info",
                            ),
                        )
                    }
                }
            }
        }

    }
})
