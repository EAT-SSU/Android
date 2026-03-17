package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class UserAndTokenResponseMapperBehaviorSpec : AppBehaviorSpec({

    given("CollegeResponse.toDomain") {
        `when`("collegeId가 null이면") {
            then("null을 반환한다") {
                CollegeResponse(collegeId = null, collegeName = "IT").toDomain().shouldBeNull()
            }
        }

        `when`("collegeName이 null이면") {
            then("null을 반환한다") {
                CollegeResponse(collegeId = 1, collegeName = null).toDomain().shouldBeNull()
            }
        }

        `when`("id/name이 모두 존재하면") {
            then("College로 매핑한다") {
                val result = CollegeResponse(collegeId = 1, collegeName = "IT").toDomain()
                result?.collegeId shouldBe 1
                result?.collegeName shouldBe "IT"
            }
        }
    }

    given("DepartmentResponse.toDomain") {
        `when`("departmentId가 null이면") {
            then("null을 반환한다") {
                DepartmentResponse(departmentId = null, departmentName = "컴퓨터학부").toDomain().shouldBeNull()
            }
        }

        `when`("departmentName이 null이면") {
            then("null을 반환한다") {
                DepartmentResponse(departmentId = 10, departmentName = null).toDomain().shouldBeNull()
            }
        }

        `when`("id/name이 모두 존재하면") {
            then("Department로 매핑한다") {
                val result = DepartmentResponse(departmentId = 10, departmentName = "컴퓨터학부").toDomain()
                result?.departmentId shouldBe 10
                result?.departmentName shouldBe "컴퓨터학부"
            }
        }
    }

    given("UserCollegeDepartmentResponse.toDomain") {
        `when`("필수 필드 중 하나라도 null이면") {
            then("null을 반환한다") {
                UserCollegeDepartmentResponse(
                    departmentId = 1,
                    departmentName = "컴퓨터학부",
                    collegeId = null,
                    collegeName = "IT",
                ).toDomain().shouldBeNull()
            }
        }

        `when`("필수 필드가 모두 존재하면") {
            then("College/Department Pair를 반환한다") {
                val result = UserCollegeDepartmentResponse(
                    departmentId = 3,
                    departmentName = "산업공학과",
                    collegeId = 2,
                    collegeName = "공과대학",
                ).toDomain()

                result?.first?.collegeId shouldBe 2
                result?.first?.collegeName shouldBe "공과대학"
                result?.second?.departmentId shouldBe 3
                result?.second?.departmentName shouldBe "산업공학과"
            }
        }
    }

    given("TokenResponse.toDomain") {
        `when`("access/refresh 토큰이 주어지면") {
            then("도메인 Token으로 매핑한다") {
                val result = TokenResponse(accessToken = "a", refreshToken = "r").toDomain()
                result.accessToken shouldBe "a"
                result.refreshToken shouldBe "r"
            }
        }
    }
})
