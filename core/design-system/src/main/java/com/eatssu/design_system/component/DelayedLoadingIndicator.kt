package com.eatssu.design_system.component

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.eatssu.design_system.preview.ThemePreviews
import com.eatssu.design_system.theme.EatssuTheme

@Composable
fun DelayedLoadingIndicator(
    modifier: Modifier = Modifier,
    delayMillis: Long = 1000L, // 기본 1초 후 표시
) {
    var show by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delayMillis)
        show = true
    }

    if (show) {
        CircularProgressIndicator()
    }
}

@ThemePreviews
@Composable
private fun DelayedLoadingIndicatorPreview() {
    EatssuTheme {
        DelayedLoadingIndicator(delayMillis = 0L)
    }
}
