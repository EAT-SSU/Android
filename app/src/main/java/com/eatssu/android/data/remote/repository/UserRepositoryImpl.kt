package com.eatssu.android.data.repository

import com.eatssu.android.data.dto.request.ChangeNicknameRequest
import com.eatssu.android.data.dto.request.UserDepartmentRequest
import com.eatssu.android.data.dto.response.toDomain
import com.eatssu.android.data.dto.response.toReviewList
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.model.isSuccess
import com.eatssu.android.data.model.map
import com.eatssu.android.data.model.orElse
import com.eatssu.android.data.model.orEmptyList
import com.eatssu.android.data.model.orNull
import com.eatssu.android.data.service.UserService
import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.repository.UserRepository
import timber.log.Timber
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService
) : UserRepository {

    override suspend fun updateUserName(body: ChangeNicknameRequest): Result<Unit> =
        when (val result = userService.changeNickname(body)) {
            is ApiResult.Success -> Result.success(Unit)
            is ApiResult.Failure -> Result.failure(Exception(result.message ?: "닉네임 변경에 실패했어요."))
            else -> Result.failure(Exception("닉네임 변경에 실패했어요."))
        }

    override suspend fun checkUserNameValidation(nickname: String): Result<Unit> {
        return when (val result = userService.checkNickname(nickname)) {
            is ApiResult.Success -> {
                if (result.data) {
                    Result.success(Unit)
                } else {
                    Result.failure(
                        Exception("올바르지 않은 닉네임이에요.")
                    )
                }
            }

            is ApiResult.Failure -> Result.failure(Exception(result.message ?: "올바르지 않은 닉네임이에요."))
            else -> Result.failure(Exception("올바르지 않은 닉네임이에요."))
        }
    }

    override suspend fun getUserReviews(): List<Review> =
        userService.getMyReviews().map { it.toReviewList() }.orEmptyList()

    override suspend fun getUserNickName(): String =
        userService.getMyInfo().map { it.nickname }.orNull() ?: ""

    override suspend fun signOut(): Boolean =
        userService.signOut().orElse(false)

    override suspend fun getTotalColleges(): List<College> =
        userService.getCollegeList()
            .map { list -> list.map { it.toDomain() } }
            .orEmptyList()

    override suspend fun getTotalDepartments(collegeId: Int): List<Department> =
        userService.getDepartmentsByCollege(collegeId)
            .map { list -> list.map { it.toDomain() } }
            .orEmptyList()

    override suspend fun getUserCollegeDepartment(): Pair<College, Department>? =
        userService.getUserCollegeDepartment().map { it.toDomain() }.orNull()

    override suspend fun setUserDepartment(departmentId: Int): Boolean {
        return userService.setUserDepartment(UserDepartmentRequest(departmentId)).isSuccess()
    }

}
