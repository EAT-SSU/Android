package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.request.CheckValidTokenRequest
import com.eatssu.android.data.remote.dto.response.TokenResponse
import com.eatssu.android.data.remote.service.OauthService
import com.eatssu.android.domain.model.ReissueTokenResult
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.DeviceType
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class OauthRepositoryImplBehaviorSpec : AppBehaviorSpec({

    given("OauthRepositoryImpl") {
        val oauthService = mockk<OauthService>()
        val repository = OauthRepositoryImpl(oauthService)

        `when`("reissueToken이 성공하면") {
            coEvery {
                oauthService.getNewToken("Bearer refresh-token")
            } returns ApiResult.Success(TokenResponse("new-access", "new-refresh"))

            then("Bearer prefix를 적용해 요청하고 성공 결과를 매핑한다") {
                runTest {
                    repository.reissueToken("refresh-token") shouldBe ReissueTokenResult.Success(
                        com.eatssu.android.domain.model.Token("new-access", "new-refresh")
                    )
                    coVerify(exactly = 1) { oauthService.getNewToken("Bearer refresh-token") }
                }
            }
        }

        `when`("reissueToken이 HTTP 실패면") {
            coEvery { oauthService.getNewToken("Bearer refresh-token") } returns ApiResult.Failure(401, "invalid")

            then("Failure(code,message)로 변환한다") {
                runTest {
                    repository.reissueToken("refresh-token") shouldBe ReissueTokenResult.Failure(
                        responseCode = 401,
                        message = "invalid",
                    )
                }
            }
        }

        `when`("reissueToken이 네트워크 오류면") {
            val error = IOException("offline")
            coEvery { oauthService.getNewToken("Bearer refresh-token") } returns ApiResult.NetworkError(error)

            then("throwable을 담은 Failure로 변환한다") {
                runTest {
                    repository.reissueToken("refresh-token") shouldBe ReissueTokenResult.Failure(throwable = error)
                }
            }
        }

        `when`("login이 성공하면") {
            coEvery { oauthService.loginWithKakao(any()) } returns ApiResult.Success(TokenResponse("a", "r"))

            then("도메인 토큰을 반환한다") {
                runTest {
                    repository.login("a@b.com", "pid", DeviceType.ANDROID) shouldBe
                        com.eatssu.android.domain.model.Token("a", "r")
                }
            }
        }

        `when`("login이 실패하면") {
            coEvery { oauthService.loginWithKakao(any()) } returns ApiResult.Failure(400, "bad")

            then("null을 반환한다") {
                runTest {
                    repository.login("a@b.com", "pid", DeviceType.ANDROID) shouldBe null
                }
            }
        }

        `when`("checkValidToken을 호출하면") {
            val body = CheckValidTokenRequest("access")
            coEvery { oauthService.checkValidToken(body) } returns ApiResult.Success(true)

            then("성공값을 그대로 반환한다") {
                runTest {
                    repository.checkValidToken("access") shouldBe true
                }
            }
        }

        `when`("checkValidToken이 실패하면") {
            val body = CheckValidTokenRequest("access")
            coEvery { oauthService.checkValidToken(body) } returns ApiResult.Failure(500, "err")

            then("기본값 false를 반환한다") {
                runTest {
                    repository.checkValidToken("access") shouldBe false
                }
            }
        }
    }
})
