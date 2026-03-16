package com.eatssu.android.di

import android.content.Context
import com.eatssu.android.BuildConfig
import com.eatssu.android.analytics.DefaultAnalyticsTracker
import com.eatssu.android.analytics.FirebaseAnalyticsTracker
import com.eatssu.android.analytics.PostHogAnalyticsTracker
import com.eatssu.common.analytics.AnalyticsTracker
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.posthog.PostHogInterface
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Provides
    @Singleton
    fun provideAnalyticsTracker(
        defaultAnalyticsTracker: DefaultAnalyticsTracker,
    ): AnalyticsTracker = defaultAnalyticsTracker

    @Provides
    @IntoSet
    fun provideFirebaseAnalyticsTracker(
        firebaseAnalyticsTracker: FirebaseAnalyticsTracker,
    ): AnalyticsTracker = firebaseAnalyticsTracker

    @Provides
    @IntoSet
    fun providePostHogAnalyticsTracker(
        postHogAnalyticsTracker: PostHogAnalyticsTracker,
    ): AnalyticsTracker = postHogAnalyticsTracker

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(): FirebaseAnalytics {
        return Firebase.analytics
    }

    @Provides
    @Singleton
    fun providePostHog(context: Context): PostHogInterface {
        val config = PostHogAndroidConfig(
            apiKey = BuildConfig.POSTHOG_API_KEY,
            host = BuildConfig.POSTHOG_HOST,
        ).apply {
            sessionReplay = true
            sessionReplayConfig.screenshot = true
            if (BuildConfig.DEBUG) {
                sessionReplayConfig.maskAllTextInputs = false
                sessionReplayConfig.maskAllImages = false
            }
        }

        return PostHogAndroid.with(context, config)
    }
}
