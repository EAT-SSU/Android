package com.eatssu.android.data.service

import com.eatssu.android.data.dto.request.ChangeNicknameRequest
import com.eatssu.android.data.dto.request.UserDepartmentRequest
import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.CollegeResponse
import com.eatssu.android.data.dto.response.DepartmentResponse
import com.eatssu.android.data.dto.response.MyNickNameResponse
import com.eatssu.android.data.dto.response.MyReviewResponse
import com.eatssu.android.data.dto.response.PartnershipResponse
import com.eatssu.android.data.dto.response.UserCollegeDepartmentResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {

    @PATCH("users/nickname") //닉네임 수정
    suspend fun changeNickname(
        @Body request: ChangeNicknameRequest,
    ): BaseResponse<Void>

    @GET("users/validate/nickname") //닉네임 중복 체크
    suspend fun checkNickname(
        @Query("nickname") nickname: String,
    ): BaseResponse<Boolean>

    @GET("users/reviews") //내가 쓴 리뷰 모아보기
    suspend fun getMyReviews(): BaseResponse<MyReviewResponse>

    @GET("users/mypage") //내 정보 모아보기
    suspend fun getMyInfo(): BaseResponse<MyNickNameResponse>

    @DELETE("users") //유저 탈퇴
    suspend fun signOut(): BaseResponse<Boolean>

    @GET("users/lookup/colleges") // 교내 모든 단과대 조회
    suspend fun getCollegeList(): BaseResponse<List<CollegeResponse>>

    @GET("users/lookup/departments") // 단과대에 따른 학과 조회
    suspend fun getDepartmentsByCollege(
        @Query("collegeId") collegeId: Int,
    ): BaseResponse<List<DepartmentResponse>>

    @GET("users/department") // 유저의 단과대, 학과 조회
    suspend fun getUserCollegeDepartment(): BaseResponse<UserCollegeDepartmentResponse>

    @POST("users/department") // 유저의 학과 설정
    suspend fun setUserDepartment(
        @Body departmentId: UserDepartmentRequest,
    ): BaseResponse<Void>

    // 유저 학과의 제휴 조회
    @GET("users/department/partnerships")
    suspend fun getUserDepartmentPartnerships(): BaseResponse<List<PartnershipResponse>>

}