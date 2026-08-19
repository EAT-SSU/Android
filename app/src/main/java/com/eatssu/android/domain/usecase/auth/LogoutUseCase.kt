package com.eatssu.android.domain.usecase.auth

import com.eatssu.android.data.local.AccountDataStore
import com.eatssu.android.data.local.FavoritePartnershipDataStore
import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.data.local.TokenStore
import com.eatssu.common.analytics.AnalyticsTracker
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val accountDataStore: AccountDataStore,
    private val tokenStore: TokenStore,
    private val settingDataStore: SettingDataStore,
    private val favoritePartnershipDataStore: FavoritePartnershipDataStore,
    private val analyticsTracker: AnalyticsTracker,
) {
    suspend operator fun invoke() {
        accountDataStore.clear()
        tokenStore.clear()
        settingDataStore.clear()
        favoritePartnershipDataStore.clear()
        analyticsTracker.resetIdentity()
    }
}
