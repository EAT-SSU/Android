package com.eatssu.android.presentation.mypage

import app.cash.turbine.test
import com.eatssu.android.R
import com.eatssu.android.domain.usecase.auth.LogoutUseCase
import com.eatssu.android.domain.usecase.auth.SignOutUseCase
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.assertToast
import com.eatssu.android.test.awaitToastEvent
import com.eatssu.common.UiState
import com.eatssu.common.enums.ToastType
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class SignOutViewModelBehaviorSpec : AppBehaviorSpec({

    given("회원탈퇴") {
        val logoutUseCase = mockk<LogoutUseCase>()
        val signOutUseCase = mockk<SignOutUseCase>()

        `when`("회원탈퇴 API가 실패하면") {
            coEvery { signOutUseCase() } returns false
            val viewModel = SignOutViewModel(logoutUseCase, signOutUseCase)

            then("Error 상태와 실패 토스트를 보낸다") {
                runTest {
                    viewModel.uiEvent.test {
                        viewModel.signOut()
                        advanceUntilIdle()

                        viewModel.uiState.value shouldBe UiState.Error
                        awaitToastEvent().assertToast(R.string.toast_sign_out_fail, ToastType.ERROR)
                        coVerify(exactly = 0) { logoutUseCase() }
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("회원탈퇴 API가 성공하면") {
            coEvery { signOutUseCase() } returns true
            coEvery { logoutUseCase() } returns Unit
            val viewModel = SignOutViewModel(logoutUseCase, signOutUseCase)

            then("성공 상태와 성공 토스트를 보낸 후 로그아웃을 수행한다") {
                runTest {
                    viewModel.uiEvent.test {
                        viewModel.signOut()
                        advanceUntilIdle()

                        viewModel.uiState.value shouldBe UiState.Success(SignOutState(isSignOuted = true))
                        awaitToastEvent().assertToast(R.string.toast_sign_out_success, ToastType.SUCCESS)
                        coVerify(exactly = 1) { logoutUseCase() }
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }
    }
})
