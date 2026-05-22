package com.eatssu.android.domain.repository

import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department

interface UserRepository {

    // 닉네임 변경
    suspend fun updateUserName(
        nickname: String,
    ): Result<Unit>

    // 유저 닉네임 중복 검사
    suspend fun checkUserNameValidation(
        nickname: String,
    ): Result<Unit>

    // 유저 닉네임 조회
    suspend fun getUserNickName(): String

    // 회원 탈퇴
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

    suspend fun getUserLanguage(): String

    suspend fun patchUserLanguage(
        language: String,
    ): Boolean
}
