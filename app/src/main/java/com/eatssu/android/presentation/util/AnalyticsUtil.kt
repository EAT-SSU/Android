package com.eatssu.android.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import com.eatssu.common.EventLogger
import com.eatssu.common.enums.ScreenId
import com.eatssu.design_system.theme.EatssuTheme

@Composable
fun TrackScreenViewEvent(
    screenId: ScreenId
) = LaunchedEffect(Unit) {
    EventLogger.screenView(screenId)
}

@Preview(showBackground = true)
@Composable
private fun TrackScreenViewEventPreview() {
    EatssuTheme {
        TrackScreenViewEvent(ScreenId.HOME_MAIN)
        Text("TrackScreenViewEvent")
    }
}
