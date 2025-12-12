package com.eatssu.common.enums

/**
 * Toast 타입을 정의하는 Enum 클래스
 * common 모듈은 Android 리소스에 접근할 수 없으므로 순수한 타입 정보만 정의
 */
enum class ToastType {
    INFO,
    SUCCESS,
    WARNING,
    ERROR
}