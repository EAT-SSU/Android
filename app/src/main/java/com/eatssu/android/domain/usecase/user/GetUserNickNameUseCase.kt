package com.eatssu.android.domain.usecase.user

import android.content.Context
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.MyNickNameResponse
import com.eatssu.android.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class GetUserNickNameUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val context: Context // SharedPreferences 접근용
) {
    suspend operator fun invoke(): Flow<BaseResponse<MyNickNameResponse>> =
        userRepository.getUserNickName().onEach { response ->
            response.result?.let { nicknameResponse ->
                MySharedPreferences.setUserName(context, nicknameResponse.nickname ?: "")
            }
        }
}
