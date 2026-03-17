package com.eatssu.android.domain.usecase.auth

import com.eatssu.android.data.local.AccountDataStore
import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.data.local.TokenStore
import com.eatssu.android.data.remote.dto.request.CheckValidTokenRequest
import com.eatssu.android.domain.model.ReissueTokenResult
import com.eatssu.android.domain.repository.OauthRepository
import com.eatssu.android.domain.repository.UserRepository
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.sampleToken
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.enums.DeviceType
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class AuthDelegatingUseCasesBehaviorSpec : AppBehaviorSpec({

    given("GetAccessTokenUseCase") {
        val tokenStore = mockk<TokenStore>()
        every { tokenStore.accessToken } returns "access-token"

        `when`("invoke를 호출하면") {
            val useCase = GetAccessTokenUseCase(tokenStore)

            then("TokenStore.accessToken 값을 반환한다") {
                useCase() shouldBe "access-token"
            }
        }
    }

    given("GetRefreshTokenUseCase") {
        val tokenStore = mockk<TokenStore>()
        every { tokenStore.refreshToken } returns "refresh-token"

        `when`("invoke를 호출하면") {
            val useCase = GetRefreshTokenUseCase(tokenStore)

            then("TokenStore.refreshToken 값을 반환한다") {
                useCase() shouldBe "refresh-token"
            }
        }
    }

    given("SetAccessTokenUseCase") {
        val tokenStore = mockk<TokenStore>()
        every { tokenStore.accessToken = "new-access" } just Runs
        val useCase = SetAccessTokenUseCase(tokenStore)

        `when`("토큰을 전달하면") {
            then("TokenStore.accessToken setter를 호출한다") {
                useCase("new-access")
                io.mockk.verify(exactly = 1) { tokenStore.accessToken = "new-access" }
            }
        }
    }

    given("SetRefreshTokenUseCase") {
        val tokenStore = mockk<TokenStore>()
        every { tokenStore.refreshToken = "new-refresh" } just Runs
        val useCase = SetRefreshTokenUseCase(tokenStore)

        `when`("토큰을 전달하면") {
            then("TokenStore.refreshToken setter를 호출한다") {
                useCase("new-refresh")
                io.mockk.verify(exactly = 1) { tokenStore.refreshToken = "new-refresh" }
            }
        }
    }

    given("LoginUseCase") {
        val oauthRepository = mockk<OauthRepository>()
        val useCase = LoginUseCase(oauthRepository)

        `when`("repository가 token을 반환하면") {
            val token = sampleToken("a", "r")
            coEvery { oauthRepository.login("a@b.com", "provider-id", DeviceType.ANDROID) } returns token

            then("동일 token을 반환한다") {
                runTest {
                    useCase("a@b.com", "provider-id", DeviceType.ANDROID) shouldBe token
                }
            }
        }

        `when`("repository가 null을 반환하면") {
            coEvery { oauthRepository.login(any(), any(), any()) } returns null

            then("null을 반환한다") {
                runTest {
                    useCase("a@b.com", "provider-id", DeviceType.ANDROID) shouldBe null
                }
            }
        }
    }

    given("LogoutUseCase") {
        val accountDataStore = mockk<AccountDataStore>()
        val tokenStore = mockk<TokenStore>()
        val settingDataStore = mockk<SettingDataStore>()
        val analyticsTracker = mockk<AnalyticsTracker>(relaxed = true)
        val useCase = LogoutUseCase(accountDataStore, tokenStore, settingDataStore, analyticsTracker)

        coJustRun { accountDataStore.clear() }
        every { tokenStore.clear() } just Runs
        coJustRun { settingDataStore.clear() }

        `when`("invoke를 호출하면") {
            then("로컬 저장소를 순서대로 clear한다") {
                runTest {
                    useCase()
                    coVerifyOrder {
                        accountDataStore.clear()
                        tokenStore.clear()
                        settingDataStore.clear()
                    }
                    io.mockk.verify(exactly = 1) { analyticsTracker.resetIdentity() }
                }
            }
        }
    }

    given("ReissueTokenUseCase") {
        val oauthRepository = mockk<OauthRepository>()
        val useCase = ReissueTokenUseCase(oauthRepository)

        `when`("repository가 성공 결과를 주면") {
            val result = ReissueTokenResult.Success(sampleToken("newA", "newR"))
            coEvery { oauthRepository.reissueToken("refresh-token") } returns result

            then("동일 결과를 반환한다") {
                runTest {
                    useCase("refresh-token") shouldBe result
                }
            }
        }

        `when`("repository가 실패 결과를 주면") {
            val result = ReissueTokenResult.Failure(responseCode = 500, message = "error")
            coEvery { oauthRepository.reissueToken("refresh-token") } returns result

            then("동일 실패 결과를 반환한다") {
                runTest {
                    useCase("refresh-token") shouldBe result
                }
            }
        }
    }

    given("GetIsAccessTokenValidUseCase") {
        val oauthRepository = mockk<OauthRepository>()
        val useCase = GetIsAccessTokenValidUseCase(oauthRepository)

        `when`("토큰 유효성 검사를 요청하면") {
            val bodySlot = slot<CheckValidTokenRequest>()
            coEvery { oauthRepository.checkValidToken(capture(bodySlot)) } returns true

            then("CheckValidTokenRequest(token)으로 위임하고 결과를 반환한다") {
                runTest {
                    useCase("user-access-token") shouldBe true
                    bodySlot.captured.token shouldBe "user-access-token"
                }
            }
        }
    }

    given("SignOutUseCase") {
        val userRepository = mockk<UserRepository>()
        val useCase = SignOutUseCase(userRepository)

        `when`("repository가 true를 반환하면") {
            coEvery { userRepository.signOut() } returns true

            then("true를 반환한다") {
                runTest {
                    useCase() shouldBe true
                    coVerify(exactly = 1) { userRepository.signOut() }
                }
            }
        }

        `when`("repository가 false를 반환하면") {
            coEvery { userRepository.signOut() } returns false

            then("false를 반환한다") {
                runTest {
                    useCase() shouldBe false
                }
            }
        }
    }
})
