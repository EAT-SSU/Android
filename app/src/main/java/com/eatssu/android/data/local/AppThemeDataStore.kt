package com.eatssu.android.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.eatssu.android.domain.model.AppTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.appThemeDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_theme")

@Singleton
class AppThemeDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        private val APP_THEME_KEY = stringPreferencesKey("app_theme")
    }

    val appTheme: Flow<AppTheme> = context.appThemeDataStore.data
        .map { preferences ->
            AppTheme.fromStringOrDefault(preferences[APP_THEME_KEY].orEmpty())
        }
        .distinctUntilChanged()

    val cachedAppTheme: AppTheme by lazy(LazyThreadSafetyMode.NONE) {
        runBlocking { appTheme.first() }
    }

    suspend fun setAppTheme(theme: AppTheme) {
        context.appThemeDataStore.edit { preferences ->
            preferences[APP_THEME_KEY] = theme.remoteValue
        }
    }
}
