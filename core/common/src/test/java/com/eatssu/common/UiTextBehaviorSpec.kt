package com.eatssu.common

import android.content.Context
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class UiTextBehaviorSpec : BehaviorSpec({

    given("UiText.StringResource") {
        val context = mockk<Context>()

        `when`("포맷 인자가 없으면") {
            every { context.getString(1) } returns "기본 문자열"

            then("리소스 문자열을 그대로 반환한다") {
                UiText.StringResource(1).asString(context) shouldBe "기본 문자열"
            }
        }

        `when`("포맷 인자에 UiText가 포함되면") {
            every { context.getString(10) } returns "내부 텍스트"
            every { context.getString(20, "내부 텍스트", 3) } returns "외부 텍스트"

            then("중첩 UiText를 재귀적으로 해석해 포맷한다") {
                val nested = UiText.StringResource(10)
                val outer = UiText.StringResource(20, nested, 3)

                outer.asString(context) shouldBe "외부 텍스트"
            }
        }

        `when`("vararg 생성자를 사용하면") {
            then("args 리스트가 생성 순서를 유지한다") {
                val text = UiText.StringResource(30, "A", 1)

                text.resId shouldBe 30
                text.args shouldBe listOf("A", 1)
            }
        }
    }

    given("UiText.DynamicString") {
        `when`("asString을 호출하면") {
            then("원본 문자열을 그대로 반환한다") {
                UiText.DynamicString("직접 입력").asString(mockk(relaxed = true)) shouldBe "직접 입력"
            }
        }
    }

    given("UiText.Empty") {
        `when`("asString을 호출하면") {
            then("빈 문자열을 반환한다") {
                UiText.Empty.asString(mockk(relaxed = true)) shouldBe ""
            }
        }
    }
})
