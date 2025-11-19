package com.eatssu.android.domain.usecase.user

import com.eatssu.android.data.local.AccountDataStore
import com.eatssu.android.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetUserNickNameUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val accountDataStore: AccountDataStore
) {
    suspend operator fun invoke(): String {
        val localName = accountDataStore.name.first() // Flow에서 첫 값 가져오기
        return localName.ifEmpty {
            val remoteNickname = userRepository.getUserNickName()
            accountDataStore.setName(remoteNickname)
            remoteNickname
        }
    }
}
