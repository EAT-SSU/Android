package com.eatssu.android.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import com.eatssu.common.UiText
import androidx.compose.ui.platform.LocalContext
import com.eatssu.design_system.theme.EatssuTheme

/**
 * Composable에서 UiText를 쉽게 Resolve할 수 있게 해주는 확장 함수
 */
@Composable
fun UiText.asString(): String {
    val context = LocalContext.current
    return asString(context)
}

@Preview(showBackground = true)
@Composable
private fun UiTextAsStringPreview() {
    EatssuTheme {
        Text(UiText.DynamicString("UiText Preview").asString())
    }
}
