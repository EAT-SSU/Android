package com.eatssu.android.domain.usecase.user

import com.eatssu.android.data.local.AccountDataStore
import com.eatssu.android.domain.repository.UserRepository
import javax.inject.Inject

class SetUserNicknameUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val accountDataStore: AccountDataStore
) {
    suspend operator fun invoke(nickname: String): Result<Unit> {
        val result = userRepository.updateUserName(nickname)
        if (result.isSuccess) {
            // 서버 닉네임 변경이 성공한 경우에만 로컬 닉네임 변경
            accountDataStore.setName(nickname)
        }
        return result
    }
}
