package com.eatssu.android.domain.usecase.user

import com.eatssu.android.domain.repository.UserRepository
import javax.inject.Inject

// 닉네임이 중복이라면 true, 중복이 아니라면 false 반환
class CheckDuplicateNicknameUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    // checkUserNameValidation의 반환 값이 true면 중복 아님, false면 중복
    // UseCase 이름에 따라 중복이면 true를 반환해야 하므로 NOT 연산자 사용
    suspend operator fun invoke(name: String): Boolean =
        !userRepository.checkUserNameValidation(name)

}