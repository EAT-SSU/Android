package com.eatssu.android.di

import com.eatssu.android.navigation.AppNotificationNavigationProvider
import com.eatssu.notification.navigation.NotificationNavigationProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    @Singleton
    abstract fun bindNotificationNavigationProvider(
        provider: AppNotificationNavigationProvider
    ): NotificationNavigationProvider
}
