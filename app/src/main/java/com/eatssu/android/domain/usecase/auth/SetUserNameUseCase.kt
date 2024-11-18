package com.eatssu.android.domain.usecase.auth

import android.content.Context
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.ChangeNicknameRequest
import com.eatssu.android.domain.repository.UserRepository
import com.eatssu.android.data.MySharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetUserNameUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(name: String): Flow<BaseResponse<Void>> {
        MySharedPreferences.setUserName(context, name)
        //Todo 이게 최선일까? 로컬에 이름 Set과 리모트의 이름 change를 usecase를 따로 만들어야하나?

        return userRepository.updateUserName(ChangeNicknameRequest(name))
    }
}