package com.eatssu.android.notification

import com.eatssu.android.alarm.FcmTokenProvider
import com.eatssu.android.alarm.PushNotificationPermission
import com.eatssu.android.alarm.PushTokenManager
import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.data.local.TokenStore
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.request.LanguageRequest
import com.eatssu.android.data.remote.service.DeletePushTokenRequest
import com.eatssu.android.data.remote.service.PushTokenRequest
import com.eatssu.android.data.remote.service.PushTokenService
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.AppLanguage
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest

class PushTokenManagerBehaviorSpec : AppBehaviorSpec({
    given("server push token registration") {
        val settings = mockk<SettingDataStore>()
        val tokens = mockk<TokenStore>()
        val service = mockk<PushTokenService>()
        val revoke = mockk<PushTokenService>()
        val fcm = mockk<FcmTokenProvider>(relaxed = true)
        val permission = mockk<PushNotificationPermission>()
        val enabled = MutableStateFlow(true)
        val language = MutableStateFlow(AppLanguage.JAPANESE)
        val manager = PushTokenManager(settings, tokens, service, revoke, fcm, permission)

        beforeEach {
            io.mockk.clearMocks(settings, tokens, service, revoke, fcm, permission)
            enabled.value = true
            every { settings.dailyNotificationStatus } returns enabled
            every { settings.appLanguage } returns language
            every { tokens.accessToken } returns "account"
            every { permission.isAllowed() } returns true
            coEvery { fcm.currentToken() } returns "device"
            coEvery { fcm.invalidatePending() } returns true
            every { fcm.requireInvalidation() } returns Unit
            coEvery { service.updateLanguage(any(), any()) } returns ApiResult.Success(Unit)
            coEvery { service.register(any(), any()) } returns ApiResult.Success(Unit)
            coEvery { service.delete(any(), any()) } returns ApiResult.Success(Unit)
            coEvery { revoke.delete(any(), any()) } returns ApiResult.Success(Unit)
        }
        then("syncs server language before registering the device") {
            runTest {
                manager.synchronize() shouldBe true
                coVerifyOrder {
                    service.updateLanguage("Bearer account", LanguageRequest("JA"))
                    service.register("Bearer account", PushTokenRequest("device", "ANDROID"))
                }
            }
        }
        then("does not register when logged out") {
            runTest {
                every { tokens.accessToken } returns ""
                manager.synchronize() shouldBe true
                coVerify(exactly = 0) { service.register(any(), any()) }
                coVerify(exactly = 0) { fcm.currentToken() }
            }
        }
        then("deletes rather than registers when the app toggle is off") {
            runTest {
                enabled.value = false
                manager.synchronize() shouldBe true
                coVerify { service.delete("Bearer account", DeletePushTokenRequest("device")) }
                coVerify(exactly = 0) { service.register(any(), any()) }
            }
        }
        then("deletes when OS or channel permission is denied") {
            runTest {
                every { permission.isAllowed() } returns false
                manager.synchronize() shouldBe true
                coVerify { service.delete(any(), any()) }
                coVerify(exactly = 0) { service.register(any(), any()) }
            }
        }
        then("retries network failures without claiming registration succeeded") {
            runTest {
                coEvery { service.register(any(), any()) } returns ApiResult.Failure(503, "Unavailable")
                manager.synchronize() shouldBe false
            }
        }
        then("does not register a token if the account changes during acquisition") {
            runTest {
                coEvery { fcm.currentToken() } coAnswers {
                    every { tokens.accessToken } returns "different-account"
                    "device"
                }
                manager.synchronize() shouldBe false
                coVerify(exactly = 0) { service.register(any(), any()) }
            }
        }
        then("revokes with the non-authenticating client and invalidates FCM on logout") {
            runTest {
                manager.revokeBeforeLogout()
                coVerifyOrder {
                    revoke.delete("Bearer account", DeletePushTokenRequest("device"))
                    fcm.invalidatePending()
                }
            }
        }
    }
})
