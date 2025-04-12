package com.eatssu.android.domain.usecase.auth

import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetIsAccessTokenValidUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): Flow<BaseResponse<Boolean>> =
        userRepository.checkUserNameValidation("qkqh") //todo api 만들어지면 수정
}