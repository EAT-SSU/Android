package com.eatssu.android.data.model

import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe
import java.io.IOException

class ApiResultBehaviorSpec : AppBehaviorSpec({

    given("ApiResult 확장 함수") {
        `when`("isSuccess를 호출하면") {
            then("Success(Unit)에서만 true를 반환한다") {
                ApiResult.Success(Unit).isSuccess() shouldBe true
                ApiResult.Failure(400, "bad").isSuccess() shouldBe false
            }
        }

        `when`("orEmptyList를 호출하면") {
            then("Success는 원본 리스트, 실패는 빈 리스트를 반환한다") {
                ApiResult.Success(listOf(1, 2, 3)).orEmptyList() shouldBe listOf(1, 2, 3)
                ApiResult.Failure(500, "err").orEmptyList<Int, List<Int>>() shouldBe emptyList()
            }
        }

        `when`("orElse를 호출하면") {
            then("Success는 데이터, 실패는 기본값을 반환한다") {
                ApiResult.Success("value").orElse("default") shouldBe "value"
                ApiResult.Failure(401, "unauthorized").orElse("default") shouldBe "default"
            }
        }

        `when`("orNull을 호출하면") {
            then("Success는 데이터, 실패는 null을 반환한다") {
                ApiResult.Success(42).orNull() shouldBe 42
                ApiResult.Failure(500, null).orNull<Int>() shouldBe null
            }
        }

        `when`("map을 호출하면") {
            then("Success는 transform되고 실패 타입은 유지된다") {
                ApiResult.Success(10).map { it * 2 } shouldBe ApiResult.Success(20)

                val failureMapped = ApiResult.Failure(404, "not found").map { it.toString() }
                (failureMapped as ApiResult.Failure).responseCode shouldBe 404
                failureMapped.message shouldBe "not found"

                val io = IOException("offline")
                val networkMapped = ApiResult.NetworkError(io).map { it.toString() }
                (networkMapped as ApiResult.NetworkError).exception shouldBe io

                val unknown = IllegalStateException("boom")
                val unknownMapped = ApiResult.UnknownError(unknown).map { it.toString() }
                (unknownMapped as ApiResult.UnknownError).exception shouldBe unknown
            }
        }
    }
})
