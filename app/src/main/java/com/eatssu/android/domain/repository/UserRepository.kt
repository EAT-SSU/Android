package com.eatssu.android.domain.repository

import com.eatssu.android.data.dto.request.ChangeNicknameRequest
import com.eatssu.android.data.dto.response.MyReviewResponse
import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department

interface UserRepository {

    suspend fun updateUserName(
        body: ChangeNicknameRequest,
    ): Boolean

    suspend fun checkUserNameValidation(
        nickname: String,
    ): Boolean

    suspend fun getUserReviews(): MyReviewResponse?
    suspend fun getUserNickName(): String
    suspend fun signOut(): Boolean

    // 모든 단과대 조회
    suspend fun getTotalColleges(): List<College>

    // 단과대에 따른 학과 조회
    suspend fun getTotalDepartments(collegeId: Int): List<Department>

    // 유저의 학과 정보 조회
    suspend fun getUserCollegeDepartment(): Pair<College, Department>?

    // 유저의 학과 설정
    suspend fun setUserDepartment(
        departmentId: Int,
    ): Boolean
}

