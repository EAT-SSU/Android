package com.eatssu.android.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.eatssu.android.domain.model.AppLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.settingDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private val DAILY_NOTIFICATION_KEY = booleanPreferencesKey("daily_notification")
        private val LANGUAGE_KEY = stringPreferencesKey("app_language")
    }

    val dailyNotificationStatus: Flow<Boolean> = context.settingDataStore.data
        .map { preferences ->
            preferences[DAILY_NOTIFICATION_KEY] ?: false // Default value is false
        }

    suspend fun setDailyNotificationStatus(status: Boolean) {
        context.settingDataStore.edit { preferences ->
            preferences[DAILY_NOTIFICATION_KEY] = status
        }
    }

    val appLanguage: Flow<AppLanguage> = context.settingDataStore.data
        .map { preferences ->
            val code = preferences[LANGUAGE_KEY] ?: ""
            AppLanguage.fromCode(code)
        }

    suspend fun setAppLanguage(language: AppLanguage) {
        context.settingDataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language.code
        }
    }

    suspend fun clear() = context.settingDataStore.edit { it.clear() }
}
