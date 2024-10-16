package com.eatssu.android.domain.usecase

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.MyInfoResponse
import com.eatssu.android.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): Flow<BaseResponse<MyInfoResponse>> =
        userRepository.getUserInfo()
}