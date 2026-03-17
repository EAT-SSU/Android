package com.eatssu.android.presentation.login

import android.content.Context
import com.eatssu.android.test.AppBehaviorSpec
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class UserApiClientBehaviorSpec : AppBehaviorSpec({

    given("UserApiClient login 확장 함수") {
        val context = mockk<Context>()
        val client = mockk<UserApiClient>()

        mockkObject(UserApiClient.Companion)
        every { UserApiClient.instance } returns client

        fun stubTalk(result: Pair<OAuthToken?, Throwable?>) {
            every { client.loginWithKakaoTalk(context, any(), any(), any(), any(), any()) } answers {
                lastArg<(OAuthToken?, Throwable?) -> Unit>().invoke(result.first, result.second)
            }
        }

        fun stubAccount(result: Pair<OAuthToken?, Throwable?>) {
            every { client.loginWithKakaoAccount(context, any(), any(), any(), any(), any(), any()) } answers {
                lastArg<(OAuthToken?, Throwable?) -> Unit>().invoke(result.first, result.second)
            }
        }

        `when`("카카오톡 로그인이 불가능하면") {
            val token = mockk<OAuthToken>()
            every { client.isKakaoTalkLoginAvailable(context) } returns false
            stubAccount(token to null)

            then("카카오 계정 로그인 결과를 반환한다") {
                runTest {
                    UserApiClient.loginWithKakao(context) shouldBe token
                    verify(exactly = 0) { client.loginWithKakaoTalk(context, any(), any(), any(), any(), any()) }
                }
            }
        }

        `when`("카카오톡 로그인 가능 + 카카오톡 로그인이 성공하면") {
            val token = mockk<OAuthToken>()
            every { client.isKakaoTalkLoginAvailable(context) } returns true
            stubTalk(token to null)

            then("카카오톡 로그인 토큰을 반환한다") {
                runTest {
                    UserApiClient.loginWithKakao(context) shouldBe token
                }
            }
        }

        `when`("카카오톡 로그인에서 취소 오류가 발생하면") {
            val cancelled = mockk<ClientError>()
            every { cancelled.reason } returns ClientErrorCause.Cancelled
            every { client.isKakaoTalkLoginAvailable(context) } returns true
            stubTalk(null to cancelled)

            then("카카오 계정 로그인으로 fallback하지 않고 오류를 그대로 던진다") {
                runTest {
                    shouldThrow<ClientError> {
                        UserApiClient.loginWithKakao(context)
                    }

                    verify(exactly = 0) {
                        client.loginWithKakaoAccount(context, any(), any(), any(), any(), any(), any())
                    }
                }
            }
        }

        `when`("카카오톡 로그인에서 일반 오류가 발생하면") {
            val token = mockk<OAuthToken>()
            val error = IllegalStateException("kakao-talk-failed")
            every { client.isKakaoTalkLoginAvailable(context) } returns true
            stubTalk(null to error)
            stubAccount(token to null)

            then("카카오 계정 로그인으로 fallback한다") {
                runTest {
                    UserApiClient.loginWithKakao(context) shouldBe token
                    verify(exactly = 1) {
                        client.loginWithKakaoAccount(context, any(), any(), any(), any(), any(), any())
                    }
                }
            }
        }
    }

    given("loginWithKakaoTalk") {
        val context = mockk<Context>()
        val client = mockk<UserApiClient>()

        mockkObject(UserApiClient.Companion)
        every { UserApiClient.instance } returns client

        `when`("callback이 error를 전달하면") {
            val error = IllegalStateException("talk-error")
            every { client.loginWithKakaoTalk(context, any(), any(), any(), any(), any()) } answers {
                lastArg<(OAuthToken?, Throwable?) -> Unit>().invoke(null, error)
            }

            then("해당 예외를 던진다") {
                runTest {
                    shouldThrow<IllegalStateException> {
                        UserApiClient.loginWithKakaoTalk(context)
                    }
                }
            }
        }

        `when`("callback이 token을 전달하면") {
            val token = mockk<OAuthToken>()
            every { client.loginWithKakaoTalk(context, any(), any(), any(), any(), any()) } answers {
                lastArg<(OAuthToken?, Throwable?) -> Unit>().invoke(token, null)
            }

            then("token을 반환한다") {
                runTest {
                    UserApiClient.loginWithKakaoTalk(context) shouldBe token
                }
            }
        }

        `when`("callback이 token/error 모두 null을 전달하면") {
            every { client.loginWithKakaoTalk(context, any(), any(), any(), any(), any()) } answers {
                lastArg<(OAuthToken?, Throwable?) -> Unit>().invoke(null, null)
            }

            then("의미 있는 RuntimeException을 던진다") {
                runTest {
                    val error = shouldThrow<RuntimeException> {
                        UserApiClient.loginWithKakaoTalk(context)
                    }
                    error.message shouldBe "kakao access token을 받아오는데 실패함, 이유는 명확하지 않음."
                }
            }
        }
    }

    given("loginWithKakaoAccount") {
        val context = mockk<Context>()
        val client = mockk<UserApiClient>()

        mockkObject(UserApiClient.Companion)
        every { UserApiClient.instance } returns client

        `when`("callback이 error를 전달하면") {
            val error = IllegalArgumentException("account-error")
            every { client.loginWithKakaoAccount(context, any(), any(), any(), any(), any(), any()) } answers {
                lastArg<(OAuthToken?, Throwable?) -> Unit>().invoke(null, error)
            }

            then("해당 예외를 던진다") {
                runTest {
                    shouldThrow<IllegalArgumentException> {
                        UserApiClient.loginWithKakaoAccount(context)
                    }
                }
            }
        }

        `when`("callback이 token을 전달하면") {
            val token = mockk<OAuthToken>()
            every { client.loginWithKakaoAccount(context, any(), any(), any(), any(), any(), any()) } answers {
                lastArg<(OAuthToken?, Throwable?) -> Unit>().invoke(token, null)
            }

            then("token을 반환한다") {
                runTest {
                    UserApiClient.loginWithKakaoAccount(context) shouldBe token
                }
            }
        }

        `when`("callback이 token/error 모두 null을 전달하면") {
            every { client.loginWithKakaoAccount(context, any(), any(), any(), any(), any(), any()) } answers {
                lastArg<(OAuthToken?, Throwable?) -> Unit>().invoke(null, null)
            }

            then("의미 있는 RuntimeException을 던진다") {
                runTest {
                    val error = shouldThrow<RuntimeException> {
                        UserApiClient.loginWithKakaoAccount(context)
                    }
                    error.message shouldBe "kakao access token을 받아오는데 실패함, 이유는 명확하지 않음."
                }
            }
        }
    }
})
