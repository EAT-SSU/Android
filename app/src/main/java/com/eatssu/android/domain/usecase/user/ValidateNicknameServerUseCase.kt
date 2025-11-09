package com.eatssu.android.domain.usecase.user

import com.eatssu.android.domain.repository.UserRepository
import javax.inject.Inject

// 서버에 요청을 보내 닉네임이 중복이지 않은지, 욕설, 금지어를 포함하지 않은지 확인
class ValidateNicknameServerUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(name: String): Result<Unit> {
        return userRepository.checkUserNameValidation(name)
    }

}