package com.eatssu.android.data.repository

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.ChangeNicknameRequest
import com.eatssu.android.data.dto.response.MyInfoResponse
import com.eatssu.android.data.dto.response.MyReviewResponse
import com.eatssu.android.data.service.UserService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val userService: UserService) :
    UserRepository {

    override suspend fun updateUserName(body: ChangeNicknameRequest): Flow<BaseResponse<Void>> =
        flow {
            emit(userService.changeNickname(body))
        }


    override suspend fun checkUserNameValidation(nickname: String): Flow<BaseResponse<Boolean>> =
        flow {
            emit(userService.checkNickname(nickname))
        }

    override suspend fun getUserReviews(): Flow<BaseResponse<MyReviewResponse>> =
        flow {
            emit(userService.getMyReviews())
        }

    override suspend fun getUserInfo(): Flow<BaseResponse<MyInfoResponse>> =
        flow {
            emit(userService.getMyInfo())
        }

    override suspend fun signOut(): Flow<BaseResponse<Boolean>> =
        flow {
            emit(userService.signOut())
        }

}
