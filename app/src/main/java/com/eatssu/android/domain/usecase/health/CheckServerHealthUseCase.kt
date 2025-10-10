package com.eatssu.android.domain.usecase.health

import com.eatssu.android.domain.repository.HealthCheckRepository
import javax.inject.Inject

class CheckServerHealthUseCase @Inject constructor(
    private val healthCheckRepository: HealthCheckRepository
) {
    suspend operator fun invoke(): Boolean =
        healthCheckRepository.checkServerHealth()
}

