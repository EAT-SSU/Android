package com.eatssu.android.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.eatssu.common.EventLogger
import com.eatssu.common.enums.ScreenId

@Composable
fun TrackScreenViewEvent(
    screenId: ScreenId
) = LaunchedEffect(Unit) {
    EventLogger.screenView(screenId)
}
