package com.eatssu.common.enums

import androidx.annotation.StringRes
import com.eatssu.common.R
import com.eatssu.common.UiText

/**
 * 착한가격업소 업종 카테고리 Enum
 * 필터 순서 명세: 전체 / 한식 / 일식 / 양식 / 중식 / 베이커리 / 기타 순
 */
enum class GoodPriceCategory(
    @StringRes val displayNameResId: Int,
    val serverKey: String?, // 서버 쿼리 파라미터 값 (null인 경우 전체)
) {
    ALL(R.string.category_good_price_all, null),
    KOREAN(R.string.category_good_price_korean, "KOREAN"),
    JAPANESE(R.string.category_good_price_japanese, "JAPANESE"),
    WESTERN(R.string.category_good_price_western, "WESTERN"),
    CHINESE(R.string.category_good_price_chinese, "CHINESE"),
    BAKERY(R.string.category_good_price_bakery, "BAKERY"),
    ETC(R.string.category_good_price_etc, "ETC");

    /** ViewModel에서 Context 없이 사용하기 위한 UiText 변환 */
    fun toUiText(): UiText = UiText.StringResource(displayNameResId)

    companion object {
        // 서버 응답 문자열로부터 Category 매핑
        fun fromServerKey(key: String?): GoodPriceCategory {
            return entries.firstOrNull { it.serverKey == key } ?: ETC
        }
    }
}
