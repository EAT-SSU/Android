package com.eatssu.android.presentation.intro

import app.cash.turbine.test
import com.eatssu.android.BuildConfig
import com.eatssu.android.R
import com.eatssu.android.data.local.AppThemePreferences
import com.eatssu.android.domain.model.AppTheme
import com.eatssu.android.domain.repository.FirebaseRemoteConfigRepository
import com.eatssu.android.domain.usecase.auth.GetAccessTokenUseCase
import com.eatssu.android.domain.usecase.health.HealthCheckUseCase
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.assertToast
import com.eatssu.android.test.awaitToastEvent
import com.eatssu.common.UiState
import com.eatssu.common.enums.ToastType
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class IntroViewModelBehaviorSpec : AppBehaviorSpec({

    given("앱 초기화") {
        val healthCheckUseCase = mockk<HealthCheckUseCase>()
        val getAccessTokenUseCase = mockk<GetAccessTokenUseCase>()
        val firebaseRemoteConfigRepository = mockk<FirebaseRemoteConfigRepository>()
        val appThemePreferences = mockk<AppThemePreferences>()

        every { appThemePreferences.getAppTheme() } returns AppTheme.DEFAULT
        every { appThemePreferences.setAppTheme(any()) } returns Unit

        `when`("강제 업데이트가 필요하고 토큰이 유효하면") {
            val minimumVersion = (BuildConfig.VERSION_CODE + 1).toLong()
            coEvery { firebaseRemoteConfigRepository.getAppTheme() } returns AppTheme.DEFAULT
            coEvery { firebaseRemoteConfigRepository.getMinimumVersionCode() } returns minimumVersion
            coEvery { healthCheckUseCase() } returns true
            every { getAccessTokenUseCase() } returns "valid-token"

            val viewModel = IntroViewModel(
                healthCheckUseCase = healthCheckUseCase,
                getAccessTokenUseCase = getAccessTokenUseCase,
                firebaseRemoteConfigRepository = firebaseRemoteConfigRepository,
                appThemePreferences = appThemePreferences,
            )

            then("강제 업데이트 결과와 유효 토큰 상태가 반영된다") {
                runTest {
                    eventually(2.seconds) {
                        viewModel.versionCheckResult.value shouldBe VersionCheckResult.ForceUpdateRequired(minimumVersion)
                        viewModel.uiState.value shouldBe UiState.Success(IntroState.ValidToken)
                    }
                }
            }
        }

        `when`("헬스체크가 실패하면") {
            coEvery { firebaseRemoteConfigRepository.getAppTheme() } returns AppTheme.DEFAULT
            coEvery { firebaseRemoteConfigRepository.getMinimumVersionCode() } returns BuildConfig.VERSION_CODE.toLong()
            coEvery { healthCheckUseCase() } returns false
            every { getAccessTokenUseCase() } returns "unused"

            val viewModel = IntroViewModel(
                healthCheckUseCase = healthCheckUseCase,
                getAccessTokenUseCase = getAccessTokenUseCase,
                firebaseRemoteConfigRepository = firebaseRemoteConfigRepository,
                appThemePreferences = appThemePreferences,
            )

            then("유효 토큰 성공 상태로 전이되지 않는다") {
                runTest {
                    eventually(2.seconds) {
                        (viewModel.uiState.value == UiState.Success(IntroState.ValidToken)) shouldBe false
                    }
                }
            }
        }

        `when`("헬스체크 성공이지만 access token이 비어있으면") {
            coEvery { firebaseRemoteConfigRepository.getAppTheme() } returns AppTheme.DEFAULT
            coEvery { firebaseRemoteConfigRepository.getMinimumVersionCode() } returns BuildConfig.VERSION_CODE.toLong()
            coEvery { healthCheckUseCase() } coAnswers {
                delay(50)
                true
            }
            every { getAccessTokenUseCase() } returns ""

            val viewModel = IntroViewModel(
                healthCheckUseCase = healthCheckUseCase,
                getAccessTokenUseCase = getAccessTokenUseCase,
                firebaseRemoteConfigRepository = firebaseRemoteConfigRepository,
                appThemePreferences = appThemePreferences,
            )

            then("토큰 오류 토스트를 보내고 Error 상태가 된다") {
                runTest {
                    viewModel.uiEvent.test {
                        awaitToastEvent().assertToast(R.string.toast_token_invalid, ToastType.INFO)
                        eventually(2.seconds) {
                            viewModel.uiState.value shouldBe UiState.Error
                        }
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("버전 체크 중 예외가 발생해도") {
            coEvery { firebaseRemoteConfigRepository.getAppTheme() } returns AppTheme.DEFAULT
            coEvery { firebaseRemoteConfigRepository.getMinimumVersionCode() } throws IllegalStateException("boom")
            coEvery { healthCheckUseCase() } returns true
            every { getAccessTokenUseCase() } returns "valid-token"

            val viewModel = IntroViewModel(
                healthCheckUseCase = healthCheckUseCase,
                getAccessTokenUseCase = getAccessTokenUseCase,
                firebaseRemoteConfigRepository = firebaseRemoteConfigRepository,
                appThemePreferences = appThemePreferences,
            )

            then("자동 로그인은 계속 진행되어 성공 상태가 된다") {
                runTest {
                    eventually(2.seconds) {
                        viewModel.versionCheckResult.value shouldBe null
                        viewModel.uiState.value shouldBe UiState.Success(IntroState.ValidToken)
                    }
                }
            }
        }

        `when`("로컬에 저장된 테마가 spring이고 원격 테마가 나중에 default로 오면") {
            every { appThemePreferences.getAppTheme() } returns AppTheme.SPRING
            coEvery { firebaseRemoteConfigRepository.getAppTheme() } coAnswers {
                delay(100)
                AppTheme.DEFAULT
            }
            coEvery { firebaseRemoteConfigRepository.getMinimumVersionCode() } returns BuildConfig.VERSION_CODE.toLong()
            coEvery { healthCheckUseCase() } returns true
            every { getAccessTokenUseCase() } returns "valid-token"

            val viewModel = IntroViewModel(
                healthCheckUseCase = healthCheckUseCase,
                getAccessTokenUseCase = getAccessTokenUseCase,
                firebaseRemoteConfigRepository = firebaseRemoteConfigRepository,
                appThemePreferences = appThemePreferences,
            )

            then("초기에는 캐시된 spring을 노출하고 이후 default를 저장한다") {
                runTest {
                    viewModel.appTheme.value shouldBe AppTheme.SPRING

                    eventually(2.seconds) {
                        viewModel.appTheme.value shouldBe AppTheme.DEFAULT
                    }

                    verify { appThemePreferences.setAppTheme(AppTheme.DEFAULT) }
                }
            }
        }

        `when`("원격 테마 로드에 실패하면") {
            every { appThemePreferences.getAppTheme() } returns AppTheme.SPRING
            coEvery { firebaseRemoteConfigRepository.getAppTheme() } throws IllegalStateException("theme-fail")
            coEvery { firebaseRemoteConfigRepository.getMinimumVersionCode() } returns BuildConfig.VERSION_CODE.toLong()
            coEvery { healthCheckUseCase() } returns true
            every { getAccessTokenUseCase() } returns "valid-token"

            val viewModel = IntroViewModel(
                healthCheckUseCase = healthCheckUseCase,
                getAccessTokenUseCase = getAccessTokenUseCase,
                firebaseRemoteConfigRepository = firebaseRemoteConfigRepository,
                appThemePreferences = appThemePreferences,
            )

            then("캐시된 테마를 유지하고 저장값은 덮어쓰지 않는다") {
                runTest {
                    eventually(2.seconds) {
                        viewModel.appTheme.value shouldBe AppTheme.SPRING
                    }

                    verify(exactly = 0) { appThemePreferences.setAppTheme(any()) }
                }
            }
        }
    }
})
