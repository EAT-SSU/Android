package com.eatssu.android.domain.usecase.auth

import com.eatssu.android.data.local.AccountDataStore
import com.eatssu.android.data.local.FavoritePartnershipDataStore
import com.eatssu.android.data.local.NotificationStore
import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.data.local.TokenStore
import com.eatssu.common.analytics.AnalyticsTracker
import javax.inject.Inject
import javax.inject.Provider
import com.eatssu.android.alarm.PushTokenManager
import com.eatssu.android.data.remote.repository.NotificationRepository

class LogoutUseCase @Inject constructor(
    private val accountDataStore: AccountDataStore,
    private val tokenStore: TokenStore,
    private val settingDataStore: SettingDataStore,
    private val favoritePartnershipDataStore: FavoritePartnershipDataStore,
    private val analyticsTracker: AnalyticsTracker,
    private val notificationStore: NotificationStore,
    private val pushTokenManager: Provider<PushTokenManager>,
    private val notificationRepository: Provider<NotificationRepository>,
) {
    suspend operator fun invoke() {
        pushTokenManager.get().revokeBeforeLogout()
        accountDataStore.clear()
        tokenStore.clear()
        pushTokenManager.get().retryLogoutCleanup()
        settingDataStore.clear()
        favoritePartnershipDataStore.clear()
        notificationStore.clear()
        notificationRepository.get().clearSession()
        analyticsTracker.resetIdentity()
    }
}
