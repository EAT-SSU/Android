package com.eatssu.android.domain.repository

import com.eatssu.android.data.dto.request.ChangeNicknameRequest
import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.MyInfoResponse
import com.eatssu.android.data.dto.response.MyReviewResponse
import kotlinx.coroutines.flow.Flow

interface UserRepository {


    suspend fun updateUserName(
        body: ChangeNicknameRequest,
    ): Flow<BaseResponse<Void>>

    suspend fun checkUserNameValidation(
        nickname: String,
    ): Flow<BaseResponse<Boolean>>

    suspend fun getUserReviews(): Flow<BaseResponse<MyReviewResponse>>
    suspend fun getUserInfo(): Flow<BaseResponse<MyInfoResponse>>
    suspend fun signOut(): Flow<BaseResponse<Boolean>>

}

