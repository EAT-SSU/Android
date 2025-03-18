package com.eatssu.android.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.eatssu.android.presentation.compose.ui.theme.EatssuTheme
import com.eatssu.android.presentation.compose.ui.theme.Primary
import com.google.firebase.annotations.concurrent.Background

@Composable
fun ComposeExample() {
    Text(
        text = "Hello, Eatssu!",
        style = EatssuTheme.typography.body1,
        color = Primary
    )
}

@Preview(showBackground = true)
@Composable
fun ComposeExamplePreview() {
    EatssuTheme {
        ComposeExample()
    }
}