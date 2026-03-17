package com.eatssu.android.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.eatssu.common.analytics.AnalyticsEvent
import com.eatssu.common.analytics.AnalyticsIdentity
import com.eatssu.common.analytics.AnalyticsTracker

val LocalAnalyticsTracker = staticCompositionLocalOf<AnalyticsTracker> { NoOpAnalyticsTracker }

@Composable
fun ProvideAnalyticsTracker(
    analyticsTracker: AnalyticsTracker,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAnalyticsTracker provides analyticsTracker,
        content = content,
    )
}

private object NoOpAnalyticsTracker : AnalyticsTracker {
    override val id: String = "noop"

    override fun track(event: AnalyticsEvent) = Unit

    override fun identify(identity: AnalyticsIdentity) = Unit

    override fun resetIdentity() = Unit
}
