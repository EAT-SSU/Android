package com.eatssu.android.repository

import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.enums.Time
import com.eatssu.android.data.model.response.BaseResponse
import com.eatssu.android.data.model.response.ChangeMenuInfoListDto
import com.eatssu.android.data.model.response.GetFixedMenuResponseDto
import com.eatssu.android.data.model.response.GetTodayMealResponseDto
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.data.service.UserService
import retrofit2.Call


class UserRepository(private val userService: UserService) {

    fun checkNickname(nickname: String):Call<String>{
        return userService.nicknameCheck(nickname)
    }
}