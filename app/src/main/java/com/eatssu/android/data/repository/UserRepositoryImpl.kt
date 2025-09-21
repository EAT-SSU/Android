package com.eatssu.android.data.repository

import com.eatssu.android.data.dto.request.ChangeNicknameRequest
import com.eatssu.android.data.dto.request.UserDepartmentRequest
import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.MyNickNameResponse
import com.eatssu.android.data.dto.response.MyReviewResponse
import com.eatssu.android.data.dto.response.toDomain
import com.eatssu.android.data.service.UserService
import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department
import com.eatssu.android.domain.repository.UserRepository
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

    override suspend fun getUserNickName(): Flow<BaseResponse<MyNickNameResponse>> =
        flow {
            emit(userService.getMyInfo())
        }

    override suspend fun signOut(): Boolean {
        return userService.signOut().result ?: false
    }

    override suspend fun getTotalColleges(): List<College> =
        userService.getCollegeList().result?.map { it.toDomain() }.orEmpty()

    override suspend fun getTotalDepartments(collegeId: Int): List<Department> =
        userService.getDepartmentsByCollege(collegeId).result?.map { it.toDomain() }.orEmpty()

    override suspend fun getUserCollegeDepartment(): Pair<College, Department> =
        userService.getUserCollegeDepartment().result?.toDomain()
            ?: throw IllegalStateException("유저 학과 정보를 불러올 수 없습니다.")

    override suspend fun setUserDepartment(departmentId: Int): BaseResponse<Void> {
        return userService.setUserDepartment(
            UserDepartmentRequest(departmentId)
        )
    }

}
