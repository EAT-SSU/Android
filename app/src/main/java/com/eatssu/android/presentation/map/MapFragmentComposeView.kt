package com.eatssu.android.presentation.map

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.eatssu.design_system.theme.EatssuTheme
import com.eatssu.design_system.theme.Primary

@Composable
fun MapFragmentComposeView() {
    Text(
        text = "Hello, Eatssu!",
        style = EatssuTheme.typography.body1,
        color = Primary
    )
}

@Preview(showBackground = true)
@Composable
fun MapFragmentComposeViewPreview() {
    EatssuTheme {
        MapFragmentComposeView()
    }
}