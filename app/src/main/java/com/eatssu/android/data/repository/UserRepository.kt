package com.eatssu.android.data.repository

import retrofit2.Callback


interface UserRepository {

    fun nicknameCheck(inputNickname: String, callback: Callback<String>)

    fun nicknameChange(inputNickname: String, callback:  Callback<Void>)
}