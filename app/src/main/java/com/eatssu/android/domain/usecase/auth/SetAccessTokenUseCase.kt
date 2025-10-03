package com.eatssu.android.domain.usecase.auth

import com.eatssu.android.data.local.TokenStore
import javax.inject.Inject

class SetAccessTokenUseCase @Inject constructor(
    private val tokenStore: TokenStore,
) {
    operator fun invoke(accessToken: String) {
        tokenStore.accessToken = accessToken
    }
}