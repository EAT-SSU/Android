package com.eatssu.android.di

import android.content.Context
import com.eatssu.android.BuildConfig
import com.eatssu.android.analytics.DefaultAnalyticsTracker
import com.eatssu.android.analytics.FirebaseAnalyticsDestination
import com.eatssu.android.analytics.PostHogAnalyticsDestination
import com.eatssu.common.analytics.AnalyticsDestination
import com.eatssu.common.analytics.AnalyticsTracker
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.posthog.PostHogInterface
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsTracker(
        defaultAnalyticsTracker: DefaultAnalyticsTracker,
    ): AnalyticsTracker

    @Binds
    @IntoSet
    abstract fun bindFirebaseAnalyticsDestination(
        firebaseAnalyticsDestination: FirebaseAnalyticsDestination,
    ): AnalyticsDestination

    @Binds
    @IntoSet
    abstract fun bindPostHogAnalyticsDestination(
        postHogAnalyticsDestination: PostHogAnalyticsDestination,
    ): AnalyticsDestination

    companion object {

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
}
