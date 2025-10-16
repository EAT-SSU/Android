package com.eatssu.android.di

import android.app.Application
import android.content.Context
import com.eatssu.android.data.repository.PreferencesRepository
import com.eatssu.android.data.repository.WidgetPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(@ApplicationContext context: Context): PreferencesRepository {
        return PreferencesRepository(context)
    }


    @Provides
    @Singleton
    fun provideWidgetPreferencesRepository(@ApplicationContext context: Context): WidgetPreferencesRepository {
        return WidgetPreferencesRepository(context)
    }
}