package com.eatssu.common.enums

import java.util.Locale

/**
 * 앱에서 지원하는 언어 목록
 */
enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeDisplayName: String
) {
    // SYSTEM("", "System Default", "시스템 언어"),  // 다국어 재활성화 시 주석 해제
    KOREAN("ko", "Korean", "한국어"),
    ENGLISH("en", "English", "English"),  // 다국어 재활성화 시 주석 해제
    JAPANESE("ja", "Japanese", "日本語"),  // 다국어 재활성화 시 주석 해제

    //     CHINESE("zh", "Chinese", "中文"),  // 다국어 재활성화 시 주석 해제
    VIETNAMESE("vi", "Vietnamese", "Tiếng Việt");  // 다국어 재활성화 시 주석 해제

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code == code } ?: KOREAN
        }
    }

    fun toLocale(): Locale? {
        return if (code.isEmpty()) null else Locale(code)
    }
}