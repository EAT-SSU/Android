package com.eatssu.common.enums

import java.util.Locale

/**
 * 앱에서 지원하는 언어 목록
 * SYSTEM은 기기 언어를 따르며, 다른 옵션은 사용자가 직접 선택한 언어
 */
enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeDisplayName: String
) {
    SYSTEM("", "System Default", "시스템 언어"),
    KOREAN("ko", "Korean", "한국어"),
    ENGLISH("en", "English", "English"),
    JAPANESE("ja", "Japanese", "日本語"),
    CHINESE("zh", "Chinese", "中文"),
    VIETNAMESE("vi", "Vietnamese", "Tiếng Việt");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code == code } ?: SYSTEM
        }
    }

    fun toLocale(): Locale? {
        return if (code.isEmpty()) null else Locale(code)
    }
}