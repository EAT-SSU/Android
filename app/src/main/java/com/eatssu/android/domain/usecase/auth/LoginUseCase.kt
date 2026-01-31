package com.eatssu.android.domain.usecase.auth

import com.eatssu.android.domain.model.Token
import com.eatssu.android.domain.repository.OauthRepository
import com.eatssu.common.enums.DeviceType
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val oauthRepository: OauthRepository,
) {
    suspend operator fun invoke(
        email: String,
        providerId: String,
        deviceType: DeviceType,
    ): Token? =
        oauthRepository.login(email, providerId, deviceType)
}
