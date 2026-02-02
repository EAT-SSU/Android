package com.eatssu.android.presentation.util

import androidx.compose.runtime.Composable
import com.eatssu.common.UiText
import androidx.compose.ui.platform.LocalContext

/**
 * Composable에서 UiText를 쉽게 Resolve할 수 있게 해주는 확장 함수
 */
@Composable
fun UiText.asString(): String {
    val context = LocalContext.current
    return asString(context)
}
