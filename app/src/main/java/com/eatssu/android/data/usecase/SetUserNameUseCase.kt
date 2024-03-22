package com.eatssu.android.data.usecase

import com.eatssu.android.App
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.ChangeNicknameRequest
import com.eatssu.android.data.repository.UserRepository
import com.eatssu.android.util.MySharedPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetUserNameUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(name: String): Flow<BaseResponse<Void>> {
        MySharedPreferences.setUserName(App.appContext, name) //Todo 이게 최선일까?

        return userRepository.updateUserName(ChangeNicknameRequest(name))
    }
}