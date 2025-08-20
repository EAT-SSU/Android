package com.eatssu.android.presentation.map

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MapFragmentComposeView() {
    Text(
        text = "Hello, Eatssu!",
        style = com.eatssu.design_system.theme.EatssuTheme.typography.body1,
        color = com.eatssu.design_system.theme.Primary
    )
}

@Preview(showBackground = true)
@Composable
fun MapFragmentComposeViewPreview() {
    com.eatssu.design_system.theme.EatssuTheme {
        MapFragmentComposeView()
    }
}