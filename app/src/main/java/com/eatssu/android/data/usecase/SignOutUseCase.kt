package com.eatssu.android.data.usecase

import com.eatssu.android.App
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.repository.UserRepository
import com.eatssu.android.util.MySharedPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): Flow<BaseResponse<Boolean>> {
        MySharedPreferences.clearUser(App.appContext)

        return userRepository.signOut()
    }
}