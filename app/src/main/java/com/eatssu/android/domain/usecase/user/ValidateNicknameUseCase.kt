package com.eatssu.android.domain.usecase.user

import javax.inject.Inject

class ValidateNicknameUseCase @Inject constructor() {

    companion object {
        private const val MIN_NICKNAME_LENGTH = 2
        private const val MAX_NICKNAME_LENGTH = 16
        private val NICKNAME_REGEX =
            Regex("^(?!\\d+\$)(?!.*\\s{2})(?!.*-{2})[가-힣A-Za-z0-9][\\s가-힣A-Za-z0-9-]{0,14}[가-힣A-Za-z0-9]?\$")
    }

    operator fun invoke(nickname: String): NicknameValidationResult {
        if (nickname.isEmpty()) {
            return NicknameValidationResult.Valid
        }

        if (nickname.length < MIN_NICKNAME_LENGTH || nickname.length > MAX_NICKNAME_LENGTH) {
            return NicknameValidationResult.Invalid("${MIN_NICKNAME_LENGTH}~${MAX_NICKNAME_LENGTH}자로 입력해주세요.")
        }

        if (nickname.matches(Regex("^\\d+\$"))) {
            return NicknameValidationResult.Invalid("숫자로만 이루어진 닉네임은 사용할 수 없습니다.")
        }

        if (nickname.contains(Regex("\\s{2}"))) {
            return NicknameValidationResult.Invalid("연속된 공백은 사용할 수 없습니다.")
        }

        if (nickname.contains(Regex("-{2}"))) {
            return NicknameValidationResult.Invalid("연속된 하이픈(-)은 사용할 수 없습니다.")
        }

        if (!nickname.matches(Regex("^[가-힣A-Za-z0-9].*"))) {
            return NicknameValidationResult.Invalid("첫 글자는 한글, 영문, 숫자만 사용할 수 있습니다.")
        }

        if (!nickname.matches(Regex(".*[가-힣A-Za-z0-9]\$"))) {
            return NicknameValidationResult.Invalid("마지막 글자는 한글, 영문, 숫자만 사용할 수 있습니다.")
        }

        if (!nickname.matches(Regex("^[가-힣A-Za-z0-9\\s-]+\$"))) {
            return NicknameValidationResult.Invalid("한글, 영문, 숫자, 공백, 하이픈(-)만 사용할 수 있습니다.")
        }

        if (!nickname.matches(NICKNAME_REGEX)) {
            return NicknameValidationResult.Invalid("${MIN_NICKNAME_LENGTH}~${MAX_NICKNAME_LENGTH}자로 입력해주세요.")
        }

        return NicknameValidationResult.Valid
    }

}


sealed class NicknameValidationResult {
    object Valid : NicknameValidationResult()
    data class Invalid(val message: String) : NicknameValidationResult()
}