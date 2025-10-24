package com.eatssu.android.domain.usecase.user

import com.eatssu.android.domain.repository.UserRepository
import javax.inject.Inject

class RemoteValidateUserNameUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(name: String): Boolean =
        userRepository.checkUserNameValidation(name)

}