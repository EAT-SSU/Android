package com.eatssu.common.enums

/**
 * 착한가격업소 업종 카테고리 Enum
 * 필터 순서 명세: 전체 / 한식 / 일식 / 양식 / 중식 / 베이커리 / 기타 순
 */
enum class GoodPriceCategory(
    val displayName: String,
    val serverKey: String?, // 서버 쿼리 파라미터 값 (null인 경우 전체)
) {
    ALL("전체", null),
    KOREAN("한식", "KOREAN"),
    JAPANESE("일식", "JAPANESE"),
    WESTERN("양식", "WESTERN"),
    CHINESE("중식", "CHINESE"),
    BAKERY("베이커리", "BAKERY"),
    ETC("기타", "ETC");

    companion object {
        // 서버 응답 문자열로부터 Category 매핑
        fun fromServerKey(key: String?): GoodPriceCategory {
            return entries.firstOrNull { it.serverKey == key } ?: ETC
        }
    }
}
