package com.eatssu.android.domain.usecase.user

import com.eatssu.android.R
import com.eatssu.android.domain.usecase.user.NicknameValidationResult.Invalid
import com.eatssu.common.UiText
import javax.inject.Inject

// 정규식을 사용해 로컬에서 닉네임 검증
class ValidateNicknameLocalUseCase @Inject constructor() {
    operator fun invoke(
        nickname: String,
        minLength: Int,
        maxLength: Int
    ): NicknameValidationResult {
        // 길이 제한
        if (nickname.length !in minLength..maxLength) {
            return Invalid(UiText.StringResource(R.string.nickname_error_length, minLength, maxLength))
        }

        // 공백 문자 금지 (띄어쓰기 제외)
        if (nickname.contains(Regex("[\\t\\n\\r\\u000B\\f\\u00A0\\u1680\\u2000-\\u200A\\u202F\\u205F\\u3000]"))) {
            return Invalid(UiText.StringResource(R.string.nickname_error_whitespace))
        }

        // 연속 띄어쓰기 금지
        if (nickname.contains(Regex(" {2,}"))) {
            return Invalid(UiText.StringResource(R.string.nickname_error_consecutive_space))
        }

        // 허용 문자 검증: 한글(가-힣, ㄱ-ㅎ), 영문(A-Z, a-z), 숫자(0-9), 띄어쓰기, _(언더바), -(하이픈)만 허용
        if (!nickname.matches(Regex("^[가-힣ㄱ-ㅎA-Za-z0-9 _-]+$"))) {
            // 이모지나 다른 특수문자가 포함된 경우
            return Invalid(UiText.StringResource(R.string.nickname_error_allowed_chars))
        }

        // 연속된 특수문자 금지 (__, --, _-, -_)
        if (nickname.contains(Regex("[_-]{2,}"))) {
            return Invalid(UiText.StringResource(R.string.nickname_error_consecutive_special))
        }

        // 숫자만으로 된 닉네임 금지
        if (nickname.matches(Regex("^\\d+$"))) {
            return Invalid(UiText.StringResource(R.string.nickname_error_only_numbers))
        }

        // 특수문자로 시작/끝 금지
        if (nickname.matches(Regex("^[_-].*")) || nickname.matches(Regex(".*[_-]$"))) {
            return Invalid(UiText.StringResource(R.string.nickname_error_special_position))
        }

        // 욕설, 비속어 필터링
        if (nickname.contains(Regex("[시씨씪슈쓔쉬쉽쒸쓉][0-9]* *[바발벌빠빡빨뻘파팔펄]|[섊좆좇졷좄좃좉졽썅춍봊]|[ㅈ조][0-9]*까|ㅅㅣㅂㅏㄹ?|ㅂ[0-9]*ㅅ|[ㅄᄲᇪᄺᄡᄣᄦᇠ]|[ㅅㅆᄴ][0-9]*[ㄲㅅㅆᄴㅂ]|[존좉][0-9 ]*나|[자보][0-9]+지|보빨|[봊봋봇봈볻봁봍] *[빨이]|[후훚훐훛훋훗훘훟훝훑][장앙]|[엠앰]창|애[미비]|애자|[가-탏탑-힣]색기|[샊샛세쉐쉑쉨쉒객갞갟갯갰갴겍겎겏겤곅곆곇곗곘곜걕걖걗걧걨걬] *[끼키퀴]|[병븅][0-9]*[신딱딲]|미친[가-닣닥-힣]|[믿밑]힌|[염옘][0-9]*병|[샊샛샜샠섹섺셋셌셐셱솃솄솈섁섂섓섔섘]기|[섹섺섻쎅쎆쎇쎽쎾쎿섁섂섃썍썎썏][스쓰]|[지야][0-9]*랄|니[애에]미|갈[0-9]*보[^가-힣]|[뻐뻑뻒뻙뻨][0-9]*[뀨큐킹낑]|꼬[0-9]*추|곧[0-9]*휴|[가-힣]슬아치|자[0-9]*박꼼|빨통|[사싸](?:이코|가지|[0-9]*까시)|육[0-9]*시[랄럴]|육[0-9]*실[알얼할헐]|즐[^가-힣]|찌[0-9]*(?:질이|랭이)|찐[0-9]*따|찐[0-9]*찌버거|창[녀놈]|[가-힣]{2,}충[^가-힣]|[가-힣]{2,}츙|부녀자|화냥년|환[양향]년|호[0-9]*[구모]|조[선센][징]|조센|[쪼쪽쪾](?:[발빨]이|[바빠]리)|盧|무현|찌끄[레래]기|(?:하악){2,}|하[앍앜]|[낭당랑앙항남담람암함] ?[가-힣]+[띠찌]|느[금급]마|文在|在寅|(?<=[^\\n])[家哥]|속냐|[tT]l[qQ]kf|Wls|[ㅂ]신|[ㅅ]발|[ㅈ]밥"))) {
            return Invalid(UiText.StringResource(R.string.nickname_error_profanity))
        }

        return NicknameValidationResult.Valid
    }

}

sealed class NicknameValidationResult {
    object Valid : NicknameValidationResult()
    data class Invalid(val message: UiText) : NicknameValidationResult()
}