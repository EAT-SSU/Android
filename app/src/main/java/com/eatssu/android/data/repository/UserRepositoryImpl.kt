package com.eatssu.android.data.repository

import com.eatssu.android.data.model.request.ChangeNicknameRequestDto
import com.eatssu.android.data.service.UserService
import retrofit2.Callback

class UserRepositoryImpl (
    private val userService: UserService
) : UserRepository {

    override fun nicknameCheck(inputNickname: String, callback: Callback<String>) {
        userService.nicknameCheck(inputNickname).enqueue(callback)
    }

    override fun nicknameChange(inputNickname: String, callback:  Callback<Void>) {
        userService.changeNickname(ChangeNicknameRequestDto(inputNickname)).enqueue(callback)
    }
}