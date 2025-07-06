package com.eatssu.android.data.repository

import com.eatssu.android.data.dto.request.ChangeNicknameRequest
import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.MyInfoResponse
import com.eatssu.android.data.dto.response.MyReviewResponse
import com.eatssu.android.data.service.UserService
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

    override suspend fun getUserInfo(): Flow<BaseResponse<MyInfoResponse>> =
        flow {
            emit(userService.getMyInfo())
        }

    override suspend fun signOut(): Flow<BaseResponse<Boolean>> =
        flow {
            emit(userService.signOut())
        }

    private val collegeMajors = mapOf(
        "인문대학" to listOf("기독교학과", "국어국문학과", "영어영문학과", "독어독문학과", "불어불문학과", "중어중문학과", "일어일문학과", "철학과", "문예창작전공", "영화예술전공", "스포츠학부"),
        "자연과학대학" to listOf("수학과", "물리학과", "화학과", "정보통계∙보험수리학과", "의생명시스템학부"),
        "법과대학" to listOf("법학과", "국제법무학과"),
        "사회과학대학" to listOf("사회복지학부", "행정학부", "정보사회학과", "언론홍보학과", "평생교육학과"),
        "경제통상대학" to listOf("경제학과", "글로벌통상학과", "금융경제학과", "국제무역학과"),
        "경영대학" to listOf("경영학부", "벤처중소기업학과", "회계학과", "금융학부", "벤처경영학과", "혁신경영학과", "복지경영학과", "회계세무학과"),
        "공과대학" to listOf("화학공학과", "신소재공학과", "전기공학부", "기계공학부", "산업ㆍ정보시스템공학과", "건축학부"),
        "IT대학" to listOf("컴퓨터학부", "전자정보공학부\n전자공학전공", "전자정보공학부\nIT융합전공", "글로벌미디어학부", "소프트웨어학부", "AI융합학부", "미디어경영학과", "정보보호학과"),
        "차세대반도체학과" to listOf("차세대반도체학과"),
        "자유전공학부" to listOf("자유전공학부")
    )

    override fun getTotalColleges(): List<String> =
        listOf("단과대") + collegeMajors.keys

    override fun getTotalMajors(college: String): List<String> =
        collegeMajors[college] ?: emptyList()

}
