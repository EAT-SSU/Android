package com.eatssu.android.domain.repository

import com.eatssu.android.data.dto.request.ChangeNicknameRequest
import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.MyNickNameResponse
import com.eatssu.android.data.dto.response.MyReviewResponse
import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun updateUserName(
        body: ChangeNicknameRequest,
    ): Flow<BaseResponse<Void>>

    suspend fun checkUserNameValidation(
        nickname: String,
    ): Flow<BaseResponse<Boolean>>

    suspend fun getUserReviews(): Flow<BaseResponse<MyReviewResponse>>
    suspend fun getUserNickName(): Flow<BaseResponse<MyNickNameResponse>>
    suspend fun signOut(): Flow<BaseResponse<Boolean>>

    // 모든 단과대 조회
    suspend fun getTotalColleges(): List<College>

    // 단과대에 따른 학과 조회
    suspend fun getTotalDepartments(collegeId: Int): List<Department>

    // 유저의 학과 정보 조회
    suspend fun getUserCollegeDepartment(): Pair<College, Department>

    // 유저의 학과 설정
    suspend fun setUserDepartment(
        departmentId: Int,
    ): BaseResponse<Void>
}

