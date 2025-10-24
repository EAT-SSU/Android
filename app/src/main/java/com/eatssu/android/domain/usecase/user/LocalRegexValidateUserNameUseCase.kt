package com.eatssu.android.domain.usecase.user

import javax.inject.Inject

class LocalRegexValidateUserNameUseCase @Inject constructor() {

    companion object {
        private const val MIN_NICKNAME_LENGTH = 2
        private const val MAX_NICKNAME_LENGTH = 16
        private val NICKNAME_REGEX =
            Regex("^(?!\\d+\$)(?!.*\\s{2})(?!.*-{2})[가-힣A-Za-z0-9][\\s가-힣A-Za-z0-9-]{0,14}[가-힣A-Za-z0-9]?\$")
    }

    operator fun invoke(nickname: String): ValidationResult {
        if (nickname.isEmpty()) {
            return ValidationResult.Valid
        }

        if (nickname.length < MIN_NICKNAME_LENGTH || nickname.length > MAX_NICKNAME_LENGTH) {
            return ValidationResult.Invalid("${MIN_NICKNAME_LENGTH}~${MAX_NICKNAME_LENGTH}자로 입력해주세요.")
        }

        if (nickname.matches(Regex("^\\d+\$"))) {
            return ValidationResult.Invalid("숫자로만 이루어진 닉네임은 사용할 수 없습니다.")
        }

        if (nickname.contains(Regex("\\s{2}"))) {
            return ValidationResult.Invalid("연속된 공백은 사용할 수 없습니다.")
        }

        if (nickname.contains(Regex("-{2}"))) {
            return ValidationResult.Invalid("연속된 하이픈(-)은 사용할 수 없습니다.")
        }

        if (!nickname.matches(Regex("^[가-힣A-Za-z0-9].*"))) {
            return ValidationResult.Invalid("첫 글자는 한글, 영문, 숫자만 사용할 수 있습니다.")
        }

        if (!nickname.matches(Regex(".*[가-힣A-Za-z0-9]\$"))) {
            return ValidationResult.Invalid("마지막 글자는 한글, 영문, 숫자만 사용할 수 있습니다.")
        }

        if (!nickname.matches(Regex("^[가-힣A-Za-z0-9\\s-]+\$"))) {
            return ValidationResult.Invalid("한글, 영문, 숫자, 공백, 하이픈(-)만 사용할 수 있습니다.")
        }

        if (!nickname.matches(NICKNAME_REGEX)) {
            return ValidationResult.Invalid("${MIN_NICKNAME_LENGTH}~${MAX_NICKNAME_LENGTH}자로 입력해주세요.")
        }

        return ValidationResult.Valid
    }

    sealed class ValidationResult {
        object Valid : ValidationResult()
        data class Invalid(val message: String) : ValidationResult()
    }
}
