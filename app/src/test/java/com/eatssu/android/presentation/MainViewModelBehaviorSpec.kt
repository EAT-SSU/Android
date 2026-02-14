package com.eatssu.android.presentation

import app.cash.turbine.test
import com.eatssu.android.R
import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department
import com.eatssu.android.domain.repository.UserRepository
import com.eatssu.android.domain.usecase.auth.LogoutUseCase
import com.eatssu.android.domain.usecase.user.GetUserCollegeDepartmentUseCase
import com.eatssu.android.domain.usecase.user.GetUserNickNameUseCase
import com.eatssu.android.domain.usecase.user.SetUserCollegeDepartmentUseCase
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.assertToast
import com.eatssu.android.test.awaitToastEvent
import com.eatssu.android.test.sampleUserInfo
import com.eatssu.common.UiState
import com.eatssu.common.enums.ToastType
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

        val college = College(collegeId = 1, collegeName = "IT")
        val department = Department(departmentId = 11, departmentName = "컴퓨터학부")
        val userInfo = sampleUserInfo(
            nickname = "eatssu",
            college = college,
            department = department,
        )

        coEvery { logoutUseCase() } returns Unit
        coEvery { getUserNickNameUseCase() } returns "eatssu"
        coEvery { getUserCollegeDepartmentUseCase() } returns userInfo
        coEvery { userRepository.getUserCollegeDepartment() } returns (college to department)
        coEvery { setUserCollegeDepartmentUseCase(college, department) } returns Unit

        `when`("학과 정보를 새로고침하면") {
            val viewModel = MainViewModel(
                logoutUseCase = logoutUseCase,
                getUserNickNameUseCase = getUserNickNameUseCase,
                setUserCollegeDepartmentUseCase = setUserCollegeDepartmentUseCase,
                userRepository = userRepository,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
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

        `when`("로그아웃을 수행하면") {
            val viewModel = MainViewModel(
                logoutUseCase = logoutUseCase,
                getUserNickNameUseCase = getUserNickNameUseCase,
                setUserCollegeDepartmentUseCase = setUserCollegeDepartmentUseCase,
                userRepository = userRepository,
                getUserCollegeDepartmentUseCase = getUserCollegeDepartmentUseCase,
            )

            then("로그아웃 유즈케이스 호출 후 성공 토스트와 LoggedOut 상태를 반영한다") {
                runTest {
                    viewModel.uiEvent.test {
                        viewModel.logOut()

                        awaitToastEvent().assertToast(R.string.toast_logout_success, ToastType.SUCCESS)
                        eventually(2.seconds) {
                            coVerify { logoutUseCase() }
                            viewModel.uiState.value shouldBe UiState.Success(MainState.LoggedOut)
                        }
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

    }
})
