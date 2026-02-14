package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.request.ChangeNicknameRequest
import com.eatssu.android.data.remote.dto.response.CollegeResponse
import com.eatssu.android.data.remote.dto.response.DepartmentResponse
import com.eatssu.android.data.remote.dto.response.MyNickNameResponse
import com.eatssu.android.data.remote.dto.response.UserCollegeDepartmentResponse
import com.eatssu.android.data.remote.service.UserService
import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryImplBehaviorSpec : AppBehaviorSpec({

    given("UserRepositoryImpl") {
        val userService = mockk<UserService>()
        val repository = UserRepositoryImpl(userService)

        `when`("닉네임 변경이 성공하면") {
            coEvery { userService.changeNickname(ChangeNicknameRequest("new")) } returns ApiResult.Success(Unit)

            then("Result.success를 반환한다") {
                runTest {
                    repository.updateUserName(ChangeNicknameRequest("new")).isSuccess shouldBe true
                }
            }
        }

        `when`("닉네임 변경이 Failure면") {
            coEvery {
                userService.changeNickname(ChangeNicknameRequest("new"))
            } returns ApiResult.Failure(400, "bad nickname")

            then("서버 메시지를 포함한 실패 Result를 반환한다") {
                runTest {
                    val result = repository.updateUserName(ChangeNicknameRequest("new"))
                    result.isFailure shouldBe true
                    result.exceptionOrNull()?.message shouldBe "bad nickname"
                }
            }
        }

        `when`("닉네임 중복검사에서 true를 받으면") {
            coEvery { userService.checkNickname("ok") } returns ApiResult.Success(true)

            then("성공 Result를 반환한다") {
                runTest {
                    repository.checkUserNameValidation("ok").isSuccess shouldBe true
                }
            }
        }

        `when`("닉네임 중복검사에서 false를 받으면") {
            coEvery { userService.checkNickname("dup") } returns ApiResult.Success(false)

            then("중복 메시지로 실패 Result를 반환한다") {
                runTest {
                    val result = repository.checkUserNameValidation("dup")
                    result.isFailure shouldBe true
                    result.exceptionOrNull()?.message shouldBe "이미 사용 중인 닉네임이에요."
                }
            }
        }

        `when`("내 닉네임 조회가 실패하면") {
            coEvery { userService.getMyInfo() } returns ApiResult.Failure(500, "err")

            then("빈 문자열을 반환한다") {
                runTest {
                    repository.getUserNickName() shouldBe ""
                }
            }
        }

        `when`("단과대/학과 목록 조회 시 null 데이터가 포함되면") {
            coEvery { userService.getCollegeList() } returns ApiResult.Success(
                listOf(
                    CollegeResponse(1, "IT"),
                    CollegeResponse(null, "invalid"),
                )
            )
            coEvery { userService.getDepartmentsByCollege(1) } returns ApiResult.Success(
                listOf(
                    DepartmentResponse(11, "컴퓨터학부"),
                    DepartmentResponse(12, null),
                )
            )

            then("mapNotNull로 유효 데이터만 반환한다") {
                runTest {
                    val colleges = repository.getTotalColleges()
                    colleges shouldHaveSize 1
                    colleges.first().collegeName shouldBe "IT"

                    val departments = repository.getTotalDepartments(1)
                    departments shouldHaveSize 1
                    departments.first().departmentName shouldBe "컴퓨터학부"
                }
            }
        }

        `when`("유저 단과대/학과 조회가 성공하면") {
            coEvery { userService.getUserCollegeDepartment() } returns ApiResult.Success(
                UserCollegeDepartmentResponse(
                    departmentId = 11,
                    departmentName = "컴퓨터학부",
                    collegeId = 1,
                    collegeName = "IT",
                )
            )

            then("도메인 Pair로 변환해 반환한다") {
                runTest {
                    val result = repository.getUserCollegeDepartment()
                    result?.first?.collegeName shouldBe "IT"
                    result?.second?.departmentName shouldBe "컴퓨터학부"
                }
            }
        }

        `when`("유저 학과 설정 요청이 성공하면") {
            coEvery { userService.setUserDepartment(any()) } returns ApiResult.Success(Unit)

            then("true를 반환한다") {
                runTest {
                    repository.setUserDepartment(11) shouldBe true
                }
            }
        }

        `when`("유저 학과 설정 요청이 실패하면") {
            coEvery { userService.setUserDepartment(any()) } returns ApiResult.Failure(500, "err")

            then("false를 반환한다") {
                runTest {
                    repository.setUserDepartment(11) shouldBe false
                }
            }
        }

        `when`("회원 탈퇴 요청이 실패하면") {
            coEvery { userService.signOut() } returns ApiResult.UnknownError(IllegalStateException("boom"))

            then("기본값 false를 반환한다") {
                runTest {
                    repository.signOut() shouldBe false
                }
            }
        }

        `when`("내 닉네임 조회가 성공하지만 nickname이 null이면") {
            coEvery { userService.getMyInfo() } returns ApiResult.Success(
                MyNickNameResponse(nickname = null, provider = "KAKAO")
            )

            then("빈 문자열을 반환한다") {
                runTest {
                    repository.getUserNickName() shouldBe ""
                }
            }
        }
    }
})
