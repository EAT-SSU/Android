package com.eatssu.android.domain.usecase.auth

import com.eatssu.android.data.local.TokenStore
import javax.inject.Inject

class GetRefreshTokenUseCase @Inject constructor(
    private val tokenStore: TokenStore
) {
    operator fun invoke(): String {
        return tokenStore.refreshToken
    }
}