package com.eatssu.android.domain.usecase.user

import android.content.Context
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.domain.repository.UserRepository
import javax.inject.Inject

class GetUserNickNameUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val context: Context // SharedPreferences 접근용
) {
    suspend operator fun invoke(): String {
        return MySharedPreferences.getUserName(context).ifEmpty {
            val remoteNickname = userRepository.getUserNickName()
            MySharedPreferences.setUserName(context, remoteNickname)
            remoteNickname
        }
    }
}
