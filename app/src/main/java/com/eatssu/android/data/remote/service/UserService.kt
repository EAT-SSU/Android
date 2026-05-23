package com.eatssu.android.data.remote.service

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.request.ChangeNicknameRequest
import com.eatssu.android.data.remote.dto.request.LanguageRequest
import com.eatssu.android.data.remote.dto.request.UserDepartmentRequest
import com.eatssu.android.data.remote.dto.response.CollegeResponse
import com.eatssu.android.data.remote.dto.response.DepartmentResponse
import com.eatssu.android.data.remote.dto.response.LanguageResponse
import com.eatssu.android.data.remote.dto.response.MyPageResponse
import com.eatssu.android.data.remote.dto.response.PartnershipResponse
import com.eatssu.android.data.remote.dto.response.UserCollegeDepartmentResponse
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
    ): ApiResult<Unit>

    @GET("users/validate/nickname") //닉네임 중복 체크
    suspend fun checkNickname(
        @Query("nickname") nickname: String,
    ): ApiResult<Boolean>

    @GET("users/mypage") //내 정보 모아보기
    suspend fun getMyInfo(): ApiResult<MyPageResponse>

    @DELETE("users") //유저 탈퇴
    suspend fun signOut(): ApiResult<Boolean>

    @GET("users/lookup/colleges") // 교내 모든 단과대 조회
    suspend fun getCollegeList(): ApiResult<List<CollegeResponse>>

    @GET("users/lookup/departments") // 단과대에 따른 학과 조회
    suspend fun getDepartmentsByCollege(
        @Query("collegeId") collegeId: Int,
    ): ApiResult<List<DepartmentResponse>>

    @GET("users/department") // 유저의 단과대, 학과 조회
    suspend fun getUserCollegeDepartment(): ApiResult<UserCollegeDepartmentResponse>

    @POST("users/department") // 유저의 학과 설정
    suspend fun setUserDepartment(
        @Body departmentId: UserDepartmentRequest,
    ): ApiResult<Unit>

    @GET("users/department/partnerships")    // 유저 학과의 제휴 조회
    suspend fun getUserDepartmentPartnerships(): ApiResult<List<PartnershipResponse>>

    @GET("users/language")    // 언어 설정 조회
    suspend fun getUserLanguage(): ApiResult<LanguageResponse>

    @PATCH("users/language")    // 언어 설정 변경
    suspend fun patchUserLanguage(
        @Body language: LanguageRequest,
    ): ApiResult<Unit>


}
