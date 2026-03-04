package com.eatssu.android.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurePrefsModule {

    @Provides
    @Singleton
    fun provideSecurePrefs(@ApplicationContext context: Context): SharedPreferences {
        return try {
            createSecurePrefs(context)
        } catch (e: Exception) {
            Timber.e(e, "Failed to create SecurePrefs. Resetting...")
            FirebaseCrashlytics.getInstance().recordException(e)

            // Clear data
            context.deleteSharedPreferences("secure_prefs")

            // Retry
            createSecurePrefs(context)
        }
    }

    private fun createSecurePrefs(context: Context): SharedPreferences {
        val masterKey = androidx.security.crypto.MasterKey.Builder(context)
            .setKeyScheme(androidx.security.crypto.MasterKey.KeyScheme.AES256_GCM)
            .build()
        return androidx.security.crypto.EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

}
