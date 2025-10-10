package com.eatssu.android.presentation


/**
 * 각 Screen에 공통적인 이벤트 타입입니다.
 * 이벤트 타입을 추가하고 싶다면 UiEvent를 상속받아 사용하세요.
 */
interface UiEvent {
    data class ShowToast(val message: String) : UiEvent
    data class NavigateToServerError(val title: String, val message: String) : UiEvent
}