package com.eatssu.android.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.eatssu.android.analytics.LocalAnalyticsTracker
import com.eatssu.common.analytics.ScreenViewEvent
import com.eatssu.common.enums.ScreenId

@Composable
fun TrackScreenViewEvent(
    screenId: ScreenId,
) {
    val analyticsTracker = LocalAnalyticsTracker.current

    LaunchedEffect(analyticsTracker, screenId) {
        analyticsTracker.track(ScreenViewEvent(screenId))
    }
}
