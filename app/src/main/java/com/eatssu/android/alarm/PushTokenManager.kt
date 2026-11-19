package com.eatssu.android.alarm

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.core.app.NotificationManagerCompat
import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.data.local.TokenStore
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.request.LanguageRequest
import com.eatssu.android.data.remote.service.DeletePushTokenRequest
import com.eatssu.android.data.remote.service.PushTokenRequest
import com.eatssu.android.data.remote.service.PushTokenService
import com.eatssu.android.di.NoToken
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushTokenManager @Inject constructor(
    private val settings: SettingDataStore,
    private val tokens: TokenStore,
    private val service: PushTokenService,
    @NoToken private val revocationService: PushTokenService,
    private val fcm: FcmTokenProvider,
    private val permission: PushNotificationPermission,
) {
    private val mutex = Mutex()

    suspend fun synchronize(): Boolean = mutex.withLock {
        // A logout while offline leaves a durable invalidation request, not an old account token.
        if (!fcm.invalidatePending()) return@withLock false
        val account = tokens.accessToken
        if (account.isBlank()) return@withLock true
        try {
            val token = fcm.currentToken()
            val enabled = settings.dailyNotificationStatus.first() && permission.isAllowed()
            if (tokens.accessToken != account) return@withLock false
            if (!enabled) {
                service.delete("Bearer $account", DeletePushTokenRequest(token)) is ApiResult.Success
            } else {
                // The server creates localized push text from User.language, not the device locale.
                val language = settings.appLanguage.first().code.uppercase()
                if (service.updateLanguage("Bearer $account", LanguageRequest(language)) !is ApiResult.Success) {
                    return@withLock false
                }
                if (tokens.accessToken != account) return@withLock false
                service.register("Bearer $account", PushTokenRequest(token, "ANDROID")) is ApiResult.Success
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            // Never log token values or exception messages containing request data.
            Timber.w("푸시 토큰 동기화 실패: %s", error.javaClass.simpleName)
            false
        }
    }

    suspend fun revokeBeforeLogout() {
        fcm.requireInvalidation()
        // Bounded cleanup: logout must still complete on an unavailable network.
        withTimeoutOrNull(5_000) {
            val account = tokens.accessToken
            if (account.isBlank()) return@withTimeoutOrNull
            try {
                val token = fcm.currentToken()
                revocationService.delete("Bearer $account", DeletePushTokenRequest(token))
                fcm.invalidatePending()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                Timber.w("로그아웃 중 푸시 토큰 해제 실패")
            }
        }
    }

    fun retryLogoutCleanup() { fcm.enqueueSync() }

    companion object {
        const val SERVER_CHANNEL = "FCMNotificationChannel"
    }
}

class FcmTokenProvider @Inject constructor(
    private val preferences: SharedPreferences,
    @ApplicationContext private val context: Context,
) {
    suspend fun currentToken(): String = FirebaseMessaging.getInstance().token.await()

    fun requireInvalidation() {
        preferences.edit(commit = true) { putBoolean("push_token_invalidation_pending", true) }
    }

    suspend fun invalidatePending(): Boolean {
        if (!preferences.getBoolean("push_token_invalidation_pending", false)) return true
        return try {
            FirebaseMessaging.getInstance().deleteToken().await()
            preferences.edit(commit = true) { remove("push_token_invalidation_pending") }
            true
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            false
        }
    }

    fun enqueueSync() { PushTokenSyncWorker.enqueue(context) }
}

class PushNotificationPermission @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun isAllowed(): Boolean = NotificationManagerCompat.from(context).areNotificationsEnabled() &&
        context.getSystemService(NotificationManager::class.java)
            .getNotificationChannel(PushTokenManager.SERVER_CHANNEL)?.importance != NotificationManager.IMPORTANCE_NONE
}
