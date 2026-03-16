package com.eatssu.android.domain.usecase.user

import com.eatssu.android.R
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.UiText
import io.kotest.matchers.shouldBe

private fun NicknameValidationResult.Invalid.resIdOrNull(): Int? =
    (message as? UiText.StringResource)?.resId

class ValidateNicknameLocalUseCaseBehaviorSpec : AppBehaviorSpec({

    given("로컬 닉네임 검증") {
        val useCase = ValidateNicknameLocalUseCase()

        `when`("길이 제한을 벗어나면") {
            then("length 에러를 반환한다") {
                val result = useCase("a", minLength = 2, maxLength = 16)
                (result is NicknameValidationResult.Invalid) shouldBe true
                (result as NicknameValidationResult.Invalid).resIdOrNull() shouldBe R.string.nickname_error_length
            }
        }

        `when`("탭/줄바꿈 등 공백 문자가 포함되면") {
            then("whitespace 에러를 반환한다") {
                val result = useCase("eat\tssu", 2, 16)
                (result is NicknameValidationResult.Invalid) shouldBe true
                (result as NicknameValidationResult.Invalid).resIdOrNull() shouldBe R.string.nickname_error_whitespace
            }
        }

        `when`("연속 공백이 포함되면") {
            then("consecutive space 에러를 반환한다") {
                val result = useCase("eat  ssu", 2, 16)
                (result is NicknameValidationResult.Invalid) shouldBe true
                (result as NicknameValidationResult.Invalid).resIdOrNull() shouldBe R.string.nickname_error_consecutive_space
            }
        }

        `when`("허용되지 않은 문자가 포함되면") {
            then("allowed chars 에러를 반환한다") {
                val result = useCase("eat😀ssu", 2, 16)
                (result is NicknameValidationResult.Invalid) shouldBe true
                (result as NicknameValidationResult.Invalid).resIdOrNull() shouldBe R.string.nickname_error_allowed_chars
            }
        }

        `when`("특수문자가 연속되면") {
            then("consecutive special 에러를 반환한다") {
                val result = useCase("eat__ssu", 2, 16)
                (result is NicknameValidationResult.Invalid) shouldBe true
                (result as NicknameValidationResult.Invalid).resIdOrNull() shouldBe R.string.nickname_error_consecutive_special
            }
        }

        `when`("숫자로만 구성되면") {
            then("only numbers 에러를 반환한다") {
                val result = useCase("123456", 2, 16)
                (result is NicknameValidationResult.Invalid) shouldBe true
                (result as NicknameValidationResult.Invalid).resIdOrNull() shouldBe R.string.nickname_error_only_numbers
            }
        }

        `when`("특수문자로 시작/종료하면") {
            then("special position 에러를 반환한다") {
                val result = useCase("_eatssu", 2, 16)
                (result is NicknameValidationResult.Invalid) shouldBe true
                (result as NicknameValidationResult.Invalid).resIdOrNull() shouldBe R.string.nickname_error_special_position
            }
        }

        `when`("욕설/비속어 패턴이 포함되면") {
            then("profanity 에러를 반환한다") {
                val result = useCase("시발", 2, 16)
                (result is NicknameValidationResult.Invalid) shouldBe true
                (result as NicknameValidationResult.Invalid).resIdOrNull() shouldBe R.string.nickname_error_profanity
            }
        }

        `when`("모든 조건을 만족하면") {
            then("Valid를 반환한다") {
                useCase("먹짱_23", 2, 16) shouldBe NicknameValidationResult.Valid
            }
        }
    }
})
