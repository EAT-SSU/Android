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

    // Local에 있는 단과대, 학과 정보 조회
    fun getTotalColleges(): List<String>
    fun getTotalDepartments(college: String): List<String>

    // 유저의 학과 기입 여부 체크
    suspend fun checkUserDepartment(): Boolean

    // 유저의 학과 정보 조회
    suspend fun getUserDepartment(): String

    // 유저의 학과 설정
    suspend fun setUserDepartment(
        departmentName: String,
    ): BaseResponse<Void>
}

