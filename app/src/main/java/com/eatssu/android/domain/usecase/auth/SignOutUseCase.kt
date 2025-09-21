package com.eatssu.android.domain.usecase.auth

import com.eatssu.android.domain.repository.UserRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): Boolean =
        userRepository.signOut()
}
