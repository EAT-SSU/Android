package com.eatssu.common

import android.content.Context
import androidx.annotation.StringRes

/**
 * UI 텍스트를 표현하는 sealed class
 * ViewModel에서 Context 없이 문자열을 다룰 수 있게 함
 */
sealed class UiText {

    /**
     * 문자열 리소스
     * @param resId 문자열 리소스 ID
     * @param args 포맷 인자
     */
    data class StringResource(
        @StringRes val resId: Int,
        val args: List<Any> = emptyList()
    ) : UiText() {
        // vararg를 사용한 편의 생성자
        constructor(@StringRes resId: Int, vararg args: Any) : this(resId, args.toList())
    }

    /**
     * 동적 문자열 ex) 사용자 입력
     * UI 텍스트는 StringResource 사용 권장
     */
    data class DynamicString(val value: String) : UiText()

    /**
     * 빈 텍스트 플레이스홀더
     */
    data object Empty : UiText()

    /**
     * Context를 사용하여 실제 문자열로 변환
     */
    fun asString(context: Context): String = when (this) {
        is StringResource -> {
            if (args.isEmpty()) {
                context.getString(resId)
            } else {
                // 중첩된 UiText 인자를 재귀적으로 변환
                val resolvedArgs = args.map { arg ->
                    when (arg) {
                        is UiText -> arg.asString(context)
                        else -> arg
                    }
                }.toTypedArray()
                context.getString(resId, *resolvedArgs)
            }
        }
        is DynamicString -> value
        is Empty -> ""
    }
}
