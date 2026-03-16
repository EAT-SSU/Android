package com.eatssu.android.presentation.login

import app.cash.turbine.test
import com.eatssu.android.R
import com.eatssu.android.analytics.AnalyticsIdentityManager
import com.eatssu.android.domain.usecase.auth.LoginUseCase
import com.eatssu.android.domain.usecase.auth.SetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.SetRefreshTokenUseCase
import com.eatssu.android.domain.usecase.user.SetUserEmailUseCase
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.assertToast
import com.eatssu.android.test.awaitToastEvent
import com.eatssu.android.test.sampleToken
import com.eatssu.common.UiState
import com.eatssu.common.enums.DeviceType
import com.eatssu.common.enums.ToastType
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelBehaviorSpec : AppBehaviorSpec({

    given("카카오 로그인") {
        val loginUseCase = mockk<LoginUseCase>()
        val setAccessTokenUseCase = mockk<SetAccessTokenUseCase>()
        val setRefreshTokenUseCase = mockk<SetRefreshTokenUseCase>()
        val setUserEmailUseCase = mockk<SetUserEmailUseCase>()
        val analyticsIdentityManager = mockk<AnalyticsIdentityManager>(relaxed = true)

        every { setAccessTokenUseCase(any()) } just Runs
        every { setRefreshTokenUseCase(any()) } just Runs
        coEvery { setUserEmailUseCase(any()) } just Runs

        `when`("토큰 발급이 실패하면") {
            val viewModel = LoginViewModel(
                loginUseCase = loginUseCase,
                setAccessTokenUseCase = setAccessTokenUseCase,
                setRefreshTokenUseCase = setRefreshTokenUseCase,
                setUserEmailUseCase = setUserEmailUseCase,
                analyticsIdentityManager = analyticsIdentityManager,
            )
            coEvery { loginUseCase("a@b.com", "pid", DeviceType.ANDROID) } returns null

            then("Error 상태와 실패 토스트 이벤트를 방출한다") {
                runTest {
                    viewModel.uiEvent.test {
                        viewModel.getKakaoLogin("a@b.com", "pid")
                        awaitToastEvent().assertToast(R.string.toast_login_failed, ToastType.ERROR)
                        eventually(2.seconds) {
                            viewModel.uiState.value shouldBe UiState.Error
                        }
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("토큰 발급이 성공하면") {
            val viewModel = LoginViewModel(
                loginUseCase = loginUseCase,
                setAccessTokenUseCase = setAccessTokenUseCase,
                setRefreshTokenUseCase = setRefreshTokenUseCase,
                setUserEmailUseCase = setUserEmailUseCase,
                analyticsIdentityManager = analyticsIdentityManager,
            )
            val token = sampleToken("acc", "ref")
            coEvery { loginUseCase("a@b.com", "pid", DeviceType.ANDROID) } returns token

            then("토큰과 이메일을 저장하고 성공 상태가 된다") {
                runTest {
                    viewModel.getKakaoLogin("a@b.com", "pid")

                    eventually(2.seconds) {
                        verify { setAccessTokenUseCase("acc") }
                        verify { setRefreshTokenUseCase("ref") }
                        coVerify { setUserEmailUseCase("a@b.com") }
                        verify { analyticsIdentityManager.identifyUser(email = "a@b.com") }
                        viewModel.uiState.value shouldBe UiState.Success(LoginState.LoginSuccess)
                    }
                }
            }
        }
    }

    given("상태 변경 함수") {
        val viewModel = LoginViewModel(
            loginUseCase = mockk(),
            setAccessTokenUseCase = mockk(relaxed = true),
            setRefreshTokenUseCase = mockk(relaxed = true),
            setUserEmailUseCase = mockk(relaxed = true),
            analyticsIdentityManager = mockk(relaxed = true),
        )

        `when`("setLoadingState를 호출하면") {
            then("Loading 상태가 된다") {
                viewModel.setLoadingState()
                viewModel.uiState.value shouldBe UiState.Loading
            }
        }

        `when`("setInitState를 호출하면") {
            then("Init 상태가 된다") {
                viewModel.setInitState()
                viewModel.uiState.value shouldBe UiState.Init
            }
        }
    }
})
