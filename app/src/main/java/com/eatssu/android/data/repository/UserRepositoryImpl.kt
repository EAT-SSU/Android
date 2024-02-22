package com.eatssu.android.data.repository

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.LoginWithKakaoRequestDto
import com.eatssu.android.data.dto.response.TokenResponseDto
import com.eatssu.android.data.service.UserService
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val userService: UserService) :
    UserRepository {
//    override suspend fun getUser(userId: Int) = flow {
//        emit(userService.getUser(userId))
//    }


}