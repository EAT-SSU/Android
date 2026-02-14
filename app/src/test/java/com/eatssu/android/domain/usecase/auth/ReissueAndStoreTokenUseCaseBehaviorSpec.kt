package com.eatssu.android.domain.usecase.auth

import com.eatssu.android.domain.model.ReissueTokenResult
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.sampleToken
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class ReissueAndStoreTokenUseCaseBehaviorSpec : AppBehaviorSpec({

    given("토큰 재발급 및 저장") {
        val getRefreshTokenUseCase = mockk<GetRefreshTokenUseCase>()
        val reissueTokenUseCase = mockk<ReissueTokenUseCase>()
        val setAccessTokenUseCase = mockk<SetAccessTokenUseCase>()
        val setRefreshTokenUseCase = mockk<SetRefreshTokenUseCase>()

        every { setAccessTokenUseCase(any()) } just Runs
        every { setRefreshTokenUseCase(any()) } just Runs

        val useCase = ReissueAndStoreTokenUseCase(
            getRefreshTokenUseCase = getRefreshTokenUseCase,
            reissueTokenUseCase = reissueTokenUseCase,
            setAccessTokenUseCase = setAccessTokenUseCase,
            setRefreshTokenUseCase = setRefreshTokenUseCase,
        )

        `when`("refresh token이 비어있으면") {
            every { getRefreshTokenUseCase() } returns " "

            then("MissingRefreshToken을 반환한다") {
                runTest {
                    useCase() shouldBe ReissueAndStoreResult.MissingRefreshToken
                    coVerify(exactly = 0) { reissueTokenUseCase(any()) }
                }
            }
        }

        `when`("재발급이 성공하고 토큰이 유효하면") {
            every { getRefreshTokenUseCase() } returns "refresh"
            coEvery { reissueTokenUseCase("refresh") } returns ReissueTokenResult.Success(
                sampleToken(access = "new-access", refresh = "new-refresh")
            )

            then("새 access token을 저장하고 Success를 반환한다") {
                runTest {
                    useCase() shouldBe ReissueAndStoreResult.Success(accessToken = "new-access")
                    verify { setAccessTokenUseCase("new-access") }
                    verify { setRefreshTokenUseCase("new-refresh") }
                }
            }
        }

        `when`("재발급 성공이지만 빈 토큰이 반환되면") {
            every { getRefreshTokenUseCase() } returns "refresh"
            coEvery { reissueTokenUseCase("refresh") } returns ReissueTokenResult.Success(
                sampleToken(access = "", refresh = "new-refresh")
            )

            then("TransientFailure를 반환하고 저장하지 않는다") {
                runTest {
                    useCase() shouldBe ReissueAndStoreResult.TransientFailure(message = "reissue returned blank tokens")
                    verify(exactly = 0) { setAccessTokenUseCase(any()) }
                    verify(exactly = 0) { setRefreshTokenUseCase(any()) }
                }
            }
        }

        `when`("재발급이 401/403으로 실패하면") {
            every { getRefreshTokenUseCase() } returns "refresh"
            coEvery { reissueTokenUseCase("refresh") } returns ReissueTokenResult.Failure(
                responseCode = 401,
                message = "invalid refresh",
            )

            then("RefreshInvalid를 반환한다") {
                runTest {
                    useCase() shouldBe ReissueAndStoreResult.RefreshInvalid(
                        responseCode = 401,
                        message = "invalid refresh",
                    )
                }
            }
        }

        `when`("재발급이 일시적 오류로 실패하면") {
            val throwable = IllegalStateException("boom")
            every { getRefreshTokenUseCase() } returns "refresh"
            coEvery { reissueTokenUseCase("refresh") } returns ReissueTokenResult.Failure(
                responseCode = 500,
                message = "server error",
                throwable = throwable,
            )

            then("TransientFailure를 반환한다") {
                runTest {
                    useCase() shouldBe ReissueAndStoreResult.TransientFailure(
                        responseCode = 500,
                        message = "server error",
                        throwable = throwable,
                    )
                }
            }
        }
    }
})
