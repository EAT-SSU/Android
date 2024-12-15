package com.eatssu.android.domain.usecase.auth

import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ValidateUserNameUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(name: String): Flow<BaseResponse<Boolean>> =
        userRepository.checkUserNameValidation(name)

}