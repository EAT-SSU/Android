package com.eatssu.android.domain.usecase.auth

import android.content.Context
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.domain.model.UserInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetUserNameUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(): UserInfo {
        val nickname = MySharedPreferences.getUserName(context)
        val college = MySharedPreferences.getUserCollege(context)
        val major = MySharedPreferences.getUserMajor(context)
        return UserInfo(nickname, college, major)
    }
}