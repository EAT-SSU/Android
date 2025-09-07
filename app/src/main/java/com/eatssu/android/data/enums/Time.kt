package com.eatssu.android.data.enums

enum class Time(val displayName: String) {
    MORNING("조식"),
    LUNCH("중식"),
    DINNER("석식");

    companion object {
        fun fromTimeEnumName(enumName: String): String {
            return Time.values().find { it.name == enumName }?.displayName ?: ""
        }
    }
}